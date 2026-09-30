package com.fcv.citas.auth.domain.port.in;

/** HU-003: cambia la contraseña usando un token de recuperación vigente y no usado, consumiéndolo. */
public interface ResetPasswordUseCase {
    void reset(String rawToken, String newPassword);
}
