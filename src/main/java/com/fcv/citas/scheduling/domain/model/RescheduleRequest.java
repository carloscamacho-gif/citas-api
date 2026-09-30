package com.fcv.citas.scheduling.domain.model;

import java.time.LocalDateTime;

/**
 * Solicitud de reprogramación (HU-019/HU-023). Mantiene "vivas" dos franjas: la original de la cita
 * ({@code previousStartAt}/{@code previousEndAt}, que sigue reservada) y la nueva retenida
 * ({@code requestedStartAt}/{@code requestedEndAt}). La cita original no se toca hasta que ADMIN decide
 * (RN-10). Inmutable: cada transición devuelve una copia.
 */
public record RescheduleRequest(Long id, Long appointmentId, Long requestedByUserId, Long requestedLocationId,
                                RescheduleStatus status, LocalDateTime previousStartAt, LocalDateTime previousEndAt,
                                LocalDateTime requestedStartAt, LocalDateTime requestedEndAt,
                                String decisionReason, Long decidedByUserId, LocalDateTime decidedAt) {

    public RescheduleRequest approved(Long adminUserId, LocalDateTime at) {
        return new RescheduleRequest(id, appointmentId, requestedByUserId, requestedLocationId,
                RescheduleStatus.APPROVED, previousStartAt, previousEndAt, requestedStartAt, requestedEndAt,
                null, adminUserId, at);
    }

    public RescheduleRequest rejected(Long adminUserId, String reason, LocalDateTime at) {
        return new RescheduleRequest(id, appointmentId, requestedByUserId, requestedLocationId,
                RescheduleStatus.REJECTED, previousStartAt, previousEndAt, requestedStartAt, requestedEndAt,
                reason, adminUserId, at);
    }
}
