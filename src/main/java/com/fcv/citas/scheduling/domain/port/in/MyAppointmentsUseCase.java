package com.fcv.citas.scheduling.domain.port.in;

import com.fcv.citas.scheduling.domain.model.AppointmentDetails;

import java.time.LocalDate;
import java.util.List;

/** HU-017: el USER consulta sus propias citas, opcionalmente filtradas por estado y fecha. */
public interface MyAppointmentsUseCase {
    List<AppointmentDetails> list(Long patientUserId, String status, LocalDate date);
}
