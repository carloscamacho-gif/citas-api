package com.fcv.citas.scheduling.domain.model;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Cita. Las horas son locales a la zona operativa (America/Bogota); la conversión a ISO-8601 con
 * offset ocurre solo en la capa web. Inmutable: cada transición devuelve una copia.
 */
public record Appointment(Long id, Long patientUserId, Long professionalId, Long locationId, Long specialtyId,
                          AppointmentStatus status, String reason, String rejectionReason,
                          LocalDateTime startAt, LocalDateTime endAt,
                          Long approvedByUserId, LocalDateTime approvedAt) {

    public int durationMinutes() {
        return (int) Duration.between(startAt, endAt).toMinutes();
    }

    public boolean isTerminal() {
        return switch (status) {
            case REJECTED, CANCELLED, COMPLETED, NO_SHOW -> true;
            case REQUESTED, APPROVED -> false;
        };
    }

    public Appointment approved(Long adminUserId, LocalDateTime at) {
        return new Appointment(id, patientUserId, professionalId, locationId, specialtyId,
                AppointmentStatus.APPROVED, reason, null, startAt, endAt, adminUserId, at);
    }

    public Appointment rejected(String motive) {
        return new Appointment(id, patientUserId, professionalId, locationId, specialtyId,
                AppointmentStatus.REJECTED, reason, motive, startAt, endAt, null, null);
    }

    public Appointment cancelled() {
        return new Appointment(id, patientUserId, professionalId, locationId, specialtyId,
                AppointmentStatus.CANCELLED, reason, rejectionReason, startAt, endAt, approvedByUserId, approvedAt);
    }
}
