package com.fcv.citas.scheduling.domain.port.in;

import com.fcv.citas.scheduling.domain.model.AppointmentDetails;

/** HU-021: el PROFESSIONAL cierra la atención de una cita propia y pasada como COMPLETED o NO_SHOW. */
public interface CloseAttentionUseCase {
    AppointmentDetails close(Long professionalUserId, Long appointmentId, AttentionOutcome outcome);
}
