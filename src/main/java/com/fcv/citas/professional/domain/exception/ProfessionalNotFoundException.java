package com.fcv.citas.professional.domain.exception;

public class ProfessionalNotFoundException extends RuntimeException {
    public ProfessionalNotFoundException(Long id) { super("No existe el profesional con id " + id); }
}
