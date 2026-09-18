package com.fcv.citas.auth.domain.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("El refresh token es inválido, expiró o fue revocado");
    }
}
