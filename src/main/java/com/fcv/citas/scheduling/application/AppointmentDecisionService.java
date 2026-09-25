package com.fcv.citas.scheduling.application;

import com.fcv.citas.scheduling.domain.exception.AppointmentNotFoundException;
import com.fcv.citas.scheduling.domain.exception.InvalidSchedulingException;
import com.fcv.citas.scheduling.domain.exception.SchedulingConflictException;
import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.AppointmentDetails;
import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.model.ChangeSource;
import com.fcv.citas.scheduling.domain.model.StatusHistoryEntry;
import com.fcv.citas.scheduling.domain.port.in.AppointmentDecision;
import com.fcv.citas.scheduling.domain.port.in.AppointmentDecisionUseCase;
import com.fcv.citas.scheduling.domain.port.in.PendingAppointmentFilter;
import com.fcv.citas.scheduling.domain.port.out.AppointmentRepositoryPort;
import com.fcv.citas.scheduling.domain.port.out.SchedulingRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/** Implementa HU-022: bandeja de citas especializadas y su aprobación/rechazo por ADMIN (RF-12, RF-18). */
@Service
public class AppointmentDecisionService implements AppointmentDecisionUseCase {

    private final AppointmentRepositoryPort appointments;
    private final SchedulingRepositoryPort scheduling;
    private final AppointmentDetailsAssembler assembler;
    private final Clock clock;

    public AppointmentDecisionService(AppointmentRepositoryPort appointments, SchedulingRepositoryPort scheduling,
                                      AppointmentDetailsAssembler assembler, Clock clock) {
        this.appointments = appointments;
        this.scheduling = scheduling;
        this.assembler = assembler;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDetails> pendingSpecialized(PendingAppointmentFilter filter) {
        return appointments.findByStatus(AppointmentStatus.REQUESTED, filter).stream()
                .map(assembler::assemble)
                .toList();
    }

    @Override
    @Transactional
    public AppointmentDetails decide(Long adminUserId, Long appointmentId, AppointmentDecision decision, String reason) {
        // Fila bloqueada: dos ADMIN decidiendo a la vez se serializan y el segundo ve el estado ya cambiado.
        Appointment current = appointments.findByIdForUpdate(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
        if (current.status() != AppointmentStatus.REQUESTED) {
            throw new SchedulingConflictException("La cita ya no está pendiente de decisión");
        }

        LocalDateTime now = LocalDateTime.now(clock);
        Appointment updated;
        String historyReason = null;
        if (decision == AppointmentDecision.REJECT) {
            if (reason == null || reason.isBlank()) {
                throw new InvalidSchedulingException("El motivo es obligatorio al rechazar una cita");
            }
            updated = current.rejected(reason.trim());
            historyReason = reason.trim();
            // RN-09: rechazar libera los slots retenidos, en la misma transacción.
            scheduling.releaseSlots(appointmentId);
        } else {
            updated = current.approved(adminUserId, now);
        }

        Appointment saved = appointments.save(updated);
        appointments.addHistory(new StatusHistoryEntry(appointmentId, saved.status(), adminUserId,
                ChangeSource.ADMIN, historyReason, now));
        return assembler.assemble(saved);
    }
}
