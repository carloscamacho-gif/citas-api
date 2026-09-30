package com.fcv.citas.auth.infrastructure.web.dto;

import com.fcv.citas.auth.domain.model.DocumentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Cuerpo de solicitud de POST /api/v1/auth/register (RF-01). */
public record RegisterRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull DocumentType documentType,
        @NotBlank String documentNumber,
        @NotBlank @Email String email,
        @NotBlank String phone,
        @NotBlank @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres") String password,
        Long insurancePlanId
) {
}
