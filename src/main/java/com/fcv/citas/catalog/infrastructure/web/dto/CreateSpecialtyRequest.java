package com.fcv.citas.catalog.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Cuerpo de POST /api/v1/admin/specialties. Coincide con adminApi.createSpecialty del frontend. */
public record CreateSpecialtyRequest(
        @NotBlank String code,
        @NotBlank String name,
        @NotNull Integer durationMinutes,
        boolean general
) {
}
