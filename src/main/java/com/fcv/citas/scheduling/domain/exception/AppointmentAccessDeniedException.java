package com.fcv.citas.scheduling.domain.exception;

/** El solicitante no es dueño de la cita (ownership, sección 8 del PRD). */
public class AppointmentAccessDeniedException extends RuntimeException {
    public AppointmentAccessDeniedException() {
        super("No tienes acceso a esta cita");
    }
}
