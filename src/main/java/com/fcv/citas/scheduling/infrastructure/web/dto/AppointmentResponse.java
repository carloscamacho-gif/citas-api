package com.fcv.citas.scheduling.infrastructure.web.dto;

import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.AppointmentDetails;
import com.fcv.citas.shared.web.ApiTime;

import java.time.OffsetDateTime;

/** Coincide con Appointment del frontend. Incluye los ids para permitir la reprogramación (elegir nueva franja). */
public record AppointmentResponse(String id, String status, String professionalName, String specialtyName,
                                  String locationName, String professionalId, String specialtyId, String locationId,
                                  OffsetDateTime startAt, int durationMinutes, String rejectionReason) {

    public static AppointmentResponse from(AppointmentDetails d) {
        Appointment a = d.appointment();
        return new AppointmentResponse(String.valueOf(a.id()), a.status().name(), d.professionalName(),
                d.specialtyName(), d.locationName(), String.valueOf(a.professionalId()),
                String.valueOf(a.specialtyId()), String.valueOf(a.locationId()),
                ApiTime.toOffset(a.startAt()), a.durationMinutes(), a.rejectionReason());
    }
}
