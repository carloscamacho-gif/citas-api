package com.fcv.citas.auth.domain.exception;

public class DocumentAlreadyUsedException extends RuntimeException {
    public DocumentAlreadyUsedException(String documentType, String documentNumber) {
        super("El documento '%s %s' ya está registrado".formatted(documentType, documentNumber));
    }
}
