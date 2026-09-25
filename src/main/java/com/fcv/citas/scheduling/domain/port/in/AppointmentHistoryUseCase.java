package com.fcv.citas.scheduling.domain.port.in;

import com.fcv.citas.scheduling.domain.model.StatusHistoryEntry;

import java.util.Collection;
import java.util.List;

public interface AppointmentHistoryUseCase {

    /** USER ve sus citas, PROFESSIONAL las suyas, ADMIN todas. */
    List<StatusHistoryEntry> history(Long requesterUserId, Collection<String> roles, Long appointmentId);
}
