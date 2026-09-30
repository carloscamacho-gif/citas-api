package com.fcv.citas.auth.infrastructure.web.dto;

/**
 * Respuesta de la solicitud de recuperación. El mensaje es siempre el mismo (no revela si el email existe).
 * {@code devToken} solo viaja en el laboratorio, cuando la exposición controlada está habilitada (RF-03).
 */
public record PasswordResetRequestResponse(String message, String devToken) {

    public static PasswordResetRequestResponse of(String devToken) {
        return new PasswordResetRequestResponse(
                "Si el correo corresponde a una cuenta, enviaremos instrucciones para restablecer la contraseña.",
                devToken);
    }
}
