package com.fcv.citas.scheduling.domain.port.in;

import com.fcv.citas.scheduling.domain.model.AppointmentDetails;

public interface BookAppointmentUseCase {
    AppointmentDetails book(Long patientUserId, BookAppointmentCommand command);
}
