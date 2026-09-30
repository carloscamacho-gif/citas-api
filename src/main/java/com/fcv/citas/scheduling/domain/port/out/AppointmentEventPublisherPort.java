package com.fcv.citas.scheduling.domain.port.out;

import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.model.ChangeSource;

/**
 * Publica un evento de cambio de estado de cita hacia una automatización externa (webhook n8n, WF-002).
 * La implementación es inerte salvo que esté habilitada por configuración y nunca debe afectar la transacción
 * de negocio si la entrega falla (S5: exponer/invocar automatización).
 */
public interface AppointmentEventPublisherPort {

    void publishStatusChanged(Long appointmentId, AppointmentStatus status, ChangeSource source, Long actorUserId);
}
