package com.fcv.citas.scheduling.domain.port.out;

import com.fcv.citas.scheduling.domain.model.RescheduleRequest;
import com.fcv.citas.scheduling.domain.port.in.RescheduleInboxFilter;

import java.util.List;
import java.util.Optional;

public interface RescheduleRepositoryPort {

    RescheduleRequest save(RescheduleRequest request);

    /** Bloquea la fila hasta el fin de la transacción: dos ADMIN decidiendo la misma solicitud se serializan. */
    Optional<RescheduleRequest> findByIdForUpdate(Long id);

    /** ¿La cita ya tiene una reprogramación PENDING? Evita solicitudes duplicadas sobre la misma cita. */
    boolean existsPendingForAppointment(Long appointmentId);

    List<RescheduleRequest> findPending(RescheduleInboxFilter filter);
}
