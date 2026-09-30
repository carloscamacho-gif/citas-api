package com.fcv.citas.catalog.domain.exception;

public class InvalidSpecialtyDurationException extends RuntimeException {
    public InvalidSpecialtyDurationException(int durationMinutes) {
        super("La duración de la especialidad debe ser 30 o 60 minutos, no %d".formatted(durationMinutes));
    }
}
