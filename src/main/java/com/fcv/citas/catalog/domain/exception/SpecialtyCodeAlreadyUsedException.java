package com.fcv.citas.catalog.domain.exception;

public class SpecialtyCodeAlreadyUsedException extends RuntimeException {
    public SpecialtyCodeAlreadyUsedException(String code) {
        super("Ya existe una especialidad con el código '%s'".formatted(code));
    }
}
