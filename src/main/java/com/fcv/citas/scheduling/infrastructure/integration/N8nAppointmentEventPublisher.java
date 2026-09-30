package com.fcv.citas.scheduling.infrastructure.integration;

import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.model.ChangeSource;
import com.fcv.citas.scheduling.domain.port.out.AppointmentEventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Adaptador opcional de S5 (WF-002): publica por webhook (n8n) los cambios de estado APPROVED/REJECTED/CANCELLED.
 *
 * <p>Inerte salvo que {@code app.n8n.webhook.enabled=true}. La entrega ocurre <em>después del commit</em> de la
 * transacción de negocio y cualquier fallo de red se registra sin propagarse: notificar nunca cambia el estado
 * de una cita. Los secretos (URL, token) viven en configuración/entorno, nunca en código ni en el cuerpo publicado.
 */
@Component
public class N8nAppointmentEventPublisher implements AppointmentEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(N8nAppointmentEventPublisher.class);
    private static final Set<AppointmentStatus> PUBLISHED =
            EnumSet.of(AppointmentStatus.APPROVED, AppointmentStatus.REJECTED, AppointmentStatus.CANCELLED);

    private final RestClient client;
    private final boolean enabled;
    private final String url;
    private final String bearerToken;

    public N8nAppointmentEventPublisher(RestClient.Builder builder,
                                        @Value("${app.n8n.webhook.enabled:false}") boolean enabled,
                                        @Value("${app.n8n.webhook.url:}") String url,
                                        @Value("${app.n8n.webhook.bearer-token:}") String bearerToken) {
        if (enabled && (url.isBlank() || bearerToken.isBlank())) {
            throw new IllegalStateException("El webhook n8n requiere app.n8n.webhook.url y app.n8n.webhook.bearer-token");
        }
        this.client = builder.build();
        this.enabled = enabled;
        this.url = url;
        this.bearerToken = bearerToken;
    }

    @Override
    public void publishStatusChanged(Long appointmentId, AppointmentStatus status, ChangeSource source, Long actorUserId) {
        if (!enabled || !PUBLISHED.contains(status)) {
            return;
        }
        Map<String, Object> event = Map.of(
                "schemaVersion", "1",
                "eventId", UUID.randomUUID().toString(),
                "eventType", "AppointmentStatusChanged",
                "appointmentId", appointmentId,
                "status", status.name(),
                "source", source.name(),
                "actorUserId", actorUserId == null ? -1L : actorUserId,
                "occurredAt", Instant.now().toString());

        Runnable send = () -> {
            try {
                client.post().uri(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + bearerToken)
                        .body(event)
                        .retrieve()
                        .toBodilessEntity();
            } catch (Exception e) {
                log.warn("No se pudo entregar el evento de la cita {}: {}", appointmentId, e.getClass().getSimpleName());
            }
        };

        // Entrega tras el commit para no notificar cambios que luego se revierten.
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    send.run();
                }
            });
        } else {
            send.run();
        }
    }
}
