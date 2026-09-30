package com.fcv.citas.scheduling.infrastructure.web.dto;

import com.fcv.citas.scheduling.domain.port.in.AttentionOutcome;
import jakarta.validation.constraints.NotNull;

/** Cuerpo de POST /api/v1/professional/appointments/{id}/close. */
public record CloseAttentionRequest(@NotNull AttentionOutcome outcome) {
}
