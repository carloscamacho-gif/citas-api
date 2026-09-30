package com.fcv.citas.auth.domain.exception;

/** Deliberadamente genérica: nunca revela si falló el email o la contraseña (evita enumeración de usuarios). */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Credenciales inválidas");
    }
}
