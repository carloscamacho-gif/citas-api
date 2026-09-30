package com.fcv.citas.scheduling.infrastructure.web.dto;

import com.fcv.citas.scheduling.domain.port.in.RescheduleCommand;
import com.fcv.citas.shared.web.ApiTime;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

/**
 * Cuerpo de POST /api/v1/appointments/{id}/reschedule. Conserva profesional y especialidad; {@code professionalId}
 * es opcional y solo sirve para rechazar un intento de cambiarlo (HU-019 CA-03). El backend calcula duración y fin.
 */
public record RescheduleRequestBody(
        Long professionalId,
        Long locationId,
        @NotNull OffsetDateTime requestedStartAt
) {

    public RescheduleCommand toCommand(Long appointmentId) {
        return new RescheduleCommand(appointmentId, professionalId, locationId, ApiTime.toLocal(requestedStartAt));
    }
}
