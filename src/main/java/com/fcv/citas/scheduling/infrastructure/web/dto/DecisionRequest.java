package com.fcv.citas.scheduling.infrastructure.web.dto;

import com.fcv.citas.scheduling.domain.port.in.AppointmentDecision;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Cuerpo de POST /api/v1/admin/appointments/{id}/decision. El motivo es obligatorio solo al rechazar. */
public record DecisionRequest(@NotNull AppointmentDecision decision, @Size(max = 500) String reason) {
}
