package com.fcv.citas.scheduling.infrastructure.web.dto;

import com.fcv.citas.scheduling.domain.port.in.BookAppointmentCommand;
import com.fcv.citas.shared.web.ApiTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

/** Cuerpo de POST /api/v1/appointments. El backend calcula duración, fin y estado. */
public record BookAppointmentRequest(
        @NotNull Long professionalId,
        @NotNull Long locationId,
        @NotNull Long specialtyId,
        @NotNull OffsetDateTime startAt,
        @Size(max = 500) String reason
) {

    public BookAppointmentCommand toCommand() {
        return new BookAppointmentCommand(professionalId, locationId, specialtyId, ApiTime.toLocal(startAt), reason);
    }
}
