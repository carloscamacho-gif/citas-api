package com.fcv.citas.catalog.domain.exception;

/** Se intentó borrar físicamente un catálogo referenciado por planes o afiliaciones. Se mapea a 409. */
public class CatalogInUseException extends RuntimeException {
    public CatalogInUseException(String message) {
        super(message);
    }
}
