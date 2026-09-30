package com.fcv.citas.catalog.domain.exception;

public class EpsPlanNotFoundException extends RuntimeException {
    public EpsPlanNotFoundException(Long id) {
        super("No existe un plan de EPS con id %d".formatted(id));
    }
}
