package com.fcv.citas.scheduling.domain.model;

/** Cita enriquecida con los nombres que necesita la interfaz (profesional, especialidad, sede). */
public record AppointmentDetails(Appointment appointment, String professionalName, String specialtyName,
                                 String locationName) {
}
