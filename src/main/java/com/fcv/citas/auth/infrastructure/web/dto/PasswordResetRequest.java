package com.fcv.citas.auth.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Cuerpo de POST /api/v1/auth/password-reset/request. */
public record PasswordResetRequest(@NotBlank @Email String email) {
}
