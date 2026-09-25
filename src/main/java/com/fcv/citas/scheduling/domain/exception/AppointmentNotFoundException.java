package com.fcv.citas.scheduling.domain.exception;

public class AppointmentNotFoundException extends RuntimeException {
    public AppointmentNotFoundException(Long id) {
        super("No existe la cita con id %d".formatted(id));
    }
}
