package com.fcv.citas.scheduling.domain.port.in;

import com.fcv.citas.scheduling.domain.model.RescheduleDetails;

/** HU-023: ADMIN aprueba o rechaza una solicitud de reprogramación PENDING. El motivo es obligatorio al rechazar (RN-04). */
public interface RescheduleDecisionUseCase {
    RescheduleDetails decide(Long adminUserId, Long rescheduleId, AppointmentDecision decision, String reason);
}
