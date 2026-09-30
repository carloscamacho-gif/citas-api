package com.fcv.citas.scheduling.domain.port.in;

import com.fcv.citas.scheduling.domain.model.RescheduleDetails;

import java.util.List;

/** HU-023: ADMIN lista las solicitudes de reprogramación en estado PENDING, filtrables (RF-18). */
public interface RescheduleInboxUseCase {
    List<RescheduleDetails> pending(RescheduleInboxFilter filter);
}
