package com.fcv.citas.scheduling.infrastructure.web.dto;

import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.AppointmentDetails;
import com.fcv.citas.shared.web.ApiTime;

import java.time.OffsetDateTime;

/** Coincide con Appointment del frontend. */
public record AppointmentResponse(String id, String status, String professionalName, String specialtyName,
                                  String locationName, OffsetDateTime startAt, int durationMinutes,
                                  String rejectionReason) {

    public static AppointmentResponse from(AppointmentDetails d) {
        Appointment a = d.appointment();
        return new AppointmentResponse(String.valueOf(a.id()), a.status().name(), d.professionalName(),
                d.specialtyName(), d.locationName(), ApiTime.toOffset(a.startAt()), a.durationMinutes(),
                a.rejectionReason());
    }
}
