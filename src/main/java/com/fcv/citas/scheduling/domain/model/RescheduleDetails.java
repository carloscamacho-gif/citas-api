package com.fcv.citas.scheduling.domain.model;

/** Solicitud de reprogramación enriquecida con los nombres que muestra la interfaz (paciente, profesional, especialidad, sede). */
public record RescheduleDetails(RescheduleRequest request, String patientName, String professionalName,
                                String specialtyName, String locationName) {
}
