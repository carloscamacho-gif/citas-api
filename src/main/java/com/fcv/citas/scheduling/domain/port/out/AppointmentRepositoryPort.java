package com.fcv.citas.scheduling.domain.port.out;

import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.model.StatusHistoryEntry;
import com.fcv.citas.scheduling.domain.port.in.PendingAppointmentFilter;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepositoryPort {

    Appointment save(Appointment appointment);

    Optional<Appointment> findById(Long id);

    /** Bloquea la fila hasta el fin de la transacción: evita dos decisiones simultáneas sobre la misma cita. */
    Optional<Appointment> findByIdForUpdate(Long id);

    List<Appointment> findByStatus(AppointmentStatus status, PendingAppointmentFilter filter);

    void addHistory(StatusHistoryEntry entry);

    List<StatusHistoryEntry> history(Long appointmentId);
}
