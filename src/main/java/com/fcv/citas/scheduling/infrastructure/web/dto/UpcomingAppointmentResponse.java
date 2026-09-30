package com.fcv.citas.scheduling.infrastructure.web.dto;

import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.AppointmentDetails;
import com.fcv.citas.shared.web.ApiTime;

import java.time.OffsetDateTime;

/**
 * Respuesta del listado de citas próximas para las automatizaciones (WF-001/WF-003). Los nombres de campo
 * coinciden con lo que consumen los workflows de n8n ({@code specialty}, {@code professional}, {@code location},
 * {@code scheduledStartAt}...).
 */
public record UpcomingAppointmentResponse(String id, String specialty, String professional, String location,
                                          int durationMinutes, String status, OffsetDateTime scheduledStartAt,
                                          OffsetDateTime scheduledEndAt) {

    public static UpcomingAppointmentResponse from(AppointmentDetails d) {
        Appointment a = d.appointment();
        return new UpcomingAppointmentResponse(String.valueOf(a.id()), d.specialtyName(), d.professionalName(),
                d.locationName(), a.durationMinutes(), a.status().name(),
                ApiTime.toOffset(a.startAt()), ApiTime.toOffset(a.endAt()));
    }
}
