package com.fcv.citas.scheduling.domain.port.in;

import com.fcv.citas.scheduling.domain.model.AppointmentDetails;

import java.time.LocalDate;
import java.util.List;

/** S5 (WF-001/WF-003): lista las citas APPROVED en un rango de días y (opcional) sede, para las automatizaciones. */
public interface UpcomingAppointmentsUseCase {
    List<AppointmentDetails> upcoming(LocalDate from, LocalDate to, Long locationId);
}
