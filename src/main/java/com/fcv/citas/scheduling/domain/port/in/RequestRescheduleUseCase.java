package com.fcv.citas.scheduling.domain.port.in;

import com.fcv.citas.scheduling.domain.model.RescheduleDetails;

/** HU-019: el USER solicita reprogramar una cita aprobada y futura propia. */
public interface RequestRescheduleUseCase {
    RescheduleDetails request(Long patientUserId, RescheduleCommand command);
}
