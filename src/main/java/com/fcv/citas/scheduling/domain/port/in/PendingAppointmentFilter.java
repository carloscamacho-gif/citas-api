package com.fcv.citas.scheduling.domain.port.in;

import java.time.LocalDate;

/** Filtros opcionales de la bandeja administrativa (RF-18). Un campo null significa "sin filtrar". */
public record PendingAppointmentFilter(Long locationId, Long professionalId, Long specialtyId, LocalDate date) {

    public static PendingAppointmentFilter none() {
        return new PendingAppointmentFilter(null, null, null, null);
    }
}
