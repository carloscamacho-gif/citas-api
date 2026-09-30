package com.fcv.citas.catalog.domain.exception;

public class SpecialtyNotFoundException extends RuntimeException {
    public SpecialtyNotFoundException(Long id) {
        super("No existe una especialidad con id %d".formatted(id));
    }
}
