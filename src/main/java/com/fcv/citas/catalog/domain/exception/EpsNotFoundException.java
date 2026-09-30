package com.fcv.citas.catalog.domain.exception;

public class EpsNotFoundException extends RuntimeException {
    public EpsNotFoundException(Long id) {
        super("No existe una EPS con id %d".formatted(id));
    }
}
