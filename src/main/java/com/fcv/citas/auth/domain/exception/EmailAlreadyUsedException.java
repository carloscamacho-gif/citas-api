package com.fcv.citas.auth.domain.exception;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String email) {
        super("El email '%s' ya está registrado".formatted(email));
    }
}
