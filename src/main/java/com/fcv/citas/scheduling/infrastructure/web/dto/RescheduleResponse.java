package com.fcv.citas.scheduling.infrastructure.web.dto;

import com.fcv.citas.scheduling.domain.model.RescheduleDetails;
import com.fcv.citas.scheduling.domain.model.RescheduleRequest;
import com.fcv.citas.shared.web.ApiTime;

import java.time.Duration;
import java.time.OffsetDateTime;

/** Solicitud de reprogramación para la interfaz (bandeja ADMIN y confirmación del USER). */
public record RescheduleResponse(String id, String appointmentId, String status, String patientName,
                                 String professionalName, String specialtyName, String locationName,
                                 OffsetDateTime previousStartAt, OffsetDateTime requestedStartAt,
                                 int durationMinutes, String decisionReason) {

    public static RescheduleResponse from(RescheduleDetails d) {
        RescheduleRequest r = d.request();
        int minutes = (int) Duration.between(r.requestedStartAt(), r.requestedEndAt()).toMinutes();
        return new RescheduleResponse(String.valueOf(r.id()), String.valueOf(r.appointmentId()), r.status().name(),
                d.patientName(), d.professionalName(), d.specialtyName(), d.locationName(),
                ApiTime.toOffset(r.previousStartAt()), ApiTime.toOffset(r.requestedStartAt()), minutes,
                r.decisionReason());
    }
}
