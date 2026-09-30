package com.fcv.citas.auth.domain.exception;

/** El token de recuperación no existe, ya fue usado o expiró. Se mapea a 400 (HU-003 CA-03). */
public class InvalidPasswordResetTokenException extends RuntimeException {
    public InvalidPasswordResetTokenException() {
        super("El enlace de recuperación no es válido o ya expiró");
    }
}
