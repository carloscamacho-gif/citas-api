package com.fcv.citas.scheduling.domain.port.in;

import java.time.LocalDateTime;

/** Datos que aporta el USER; el backend calcula duración, fin y estado (RF-11/RF-12). */
public record BookAppointmentCommand(Long professionalId, Long locationId, Long specialtyId,
                                     LocalDateTime startAt, String reason) {
}
