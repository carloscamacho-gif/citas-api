package com.fcv.citas.catalog.domain.exception;

/** Código ya usado por otra EPS o plan del catálogo. Se mapea a 409. */
public class CatalogCodeAlreadyUsedException extends RuntimeException {
    public CatalogCodeAlreadyUsedException(String code) {
        super("El código '%s' ya está en uso".formatted(code));
    }
}
