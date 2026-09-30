package com.fcv.citas.scheduling.domain.port.in;

import java.time.LocalDate;

/** Filtros de la bandeja de reprogramaciones pendientes (RF-18): sede, profesional, especialidad, día. */
public record RescheduleInboxFilter(Long locationId, Long professionalId, Long specialtyId, LocalDate date) {
}
