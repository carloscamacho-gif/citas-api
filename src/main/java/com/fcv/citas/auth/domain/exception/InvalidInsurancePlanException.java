package com.fcv.citas.auth.domain.exception;

public class InvalidInsurancePlanException extends RuntimeException {
    public InvalidInsurancePlanException(Long id) {
        super("El plan de afiliación %d no existe o está inactivo".formatted(id));
    }
}
