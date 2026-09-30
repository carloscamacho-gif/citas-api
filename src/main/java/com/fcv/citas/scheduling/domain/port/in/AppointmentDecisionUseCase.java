package com.fcv.citas.scheduling.domain.port.in;

import com.fcv.citas.scheduling.domain.model.AppointmentDetails;

import java.util.List;

public interface AppointmentDecisionUseCase {

    List<AppointmentDetails> pendingSpecialized(PendingAppointmentFilter filter);

    AppointmentDetails decide(Long adminUserId, Long appointmentId, AppointmentDecision decision, String reason);
}
