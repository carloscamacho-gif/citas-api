package com.fcv.citas.scheduling.domain.port.in;

import com.fcv.citas.scheduling.domain.model.AppointmentDetails;

/** HU-018: el USER cancela una cita futura no terminal propia; libera los slots y audita el cambio. */
public interface CancelAppointmentUseCase {
    AppointmentDetails cancel(Long patientUserId, Long appointmentId);
}
