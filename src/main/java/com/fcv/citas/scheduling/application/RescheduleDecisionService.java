package com.fcv.citas.scheduling.application;

import com.fcv.citas.scheduling.domain.exception.AppointmentNotFoundException;
import com.fcv.citas.scheduling.domain.exception.InvalidSchedulingException;
import com.fcv.citas.scheduling.domain.exception.RescheduleRequestNotFoundException;
import com.fcv.citas.scheduling.domain.exception.SchedulingConflictException;
import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.model.ChangeSource;
import com.fcv.citas.scheduling.domain.model.RescheduleDetails;
import com.fcv.citas.scheduling.domain.model.RescheduleRequest;
import com.fcv.citas.scheduling.domain.model.RescheduleStatus;
import com.fcv.citas.scheduling.domain.model.StatusHistoryEntry;
import com.fcv.citas.scheduling.domain.port.in.AppointmentDecision;
import com.fcv.citas.scheduling.domain.port.in.RescheduleDecisionUseCase;
import com.fcv.citas.scheduling.domain.port.in.RescheduleInboxFilter;
import com.fcv.citas.scheduling.domain.port.in.RescheduleInboxUseCase;
import com.fcv.citas.scheduling.domain.port.out.AppointmentRepositoryPort;
import com.fcv.citas.scheduling.domain.port.out.RescheduleRepositoryPort;
import com.fcv.citas.scheduling.domain.port.out.SchedulingRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementa HU-023: bandeja de reprogramaciones pendientes y su aprobación/rechazo por ADMIN (RF-15, RF-18, RN-04, RN-09, RN-10).
 *
 * <p>Aprobar libera la franja original y confirma la nueva (ya retenida) sobre la misma cita; rechazar libera
 * solo la franja provisional nueva y deja la cita intacta. Ambas operaciones son atómicas.
 */
@Service
public class RescheduleDecisionService implements RescheduleInboxUseCase, RescheduleDecisionUseCase {

    private final RescheduleRepositoryPort reschedules;
    private final AppointmentRepositoryPort appointments;
    private final SchedulingRepositoryPort scheduling;
    private final RescheduleDetailsAssembler assembler;
    private final Clock clock;

    public RescheduleDecisionService(RescheduleRepositoryPort reschedules, AppointmentRepositoryPort appointments,
                                     SchedulingRepositoryPort scheduling, RescheduleDetailsAssembler assembler,
                                     Clock clock) {
        this.reschedules = reschedules;
        this.appointments = appointments;
        this.scheduling = scheduling;
        this.assembler = assembler;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RescheduleDetails> pending(RescheduleInboxFilter filter) {
        return reschedules.findPending(filter).stream()
                .map(r -> assembler.assemble(r, appointments.findById(r.appointmentId()).orElseThrow(
                        () -> new AppointmentNotFoundException(r.appointmentId()))))
                .toList();
    }

    @Override
    @Transactional
    public RescheduleDetails decide(Long adminUserId, Long rescheduleId, AppointmentDecision decision, String reason) {
        RescheduleRequest current = reschedules.findByIdForUpdate(rescheduleId)
                .orElseThrow(() -> new RescheduleRequestNotFoundException(rescheduleId));
        if (current.status() != RescheduleStatus.PENDING) {
            throw new SchedulingConflictException("La reprogramación ya fue decidida");
        }
        Appointment appointment = appointments.findByIdForUpdate(current.appointmentId())
                .orElseThrow(() -> new AppointmentNotFoundException(current.appointmentId()));

        LocalDateTime now = LocalDateTime.now(clock);
        RescheduleRequest decided;
        Appointment resultAppointment = appointment;
        String historyReason;

        if (decision == AppointmentDecision.REJECT) {
            if (reason == null || reason.isBlank()) {
                throw new InvalidSchedulingException("El motivo es obligatorio al rechazar una reprogramación");
            }
            // RN-09/RN-10: libera solo la franja nueva provisional; la cita original queda intacta.
            scheduling.releaseSlotsInRange(current.appointmentId(), current.requestedStartAt(), current.requestedEndAt());
            decided = current.rejected(adminUserId, reason.trim(), now);
            historyReason = "Reprogramación rechazada: " + reason.trim();
        } else {
            // Libera la franja antigua y confirma la nueva (ya retenida); actualiza el horario de la cita.
            scheduling.releaseSlotsInRange(current.appointmentId(), current.previousStartAt(), current.previousEndAt());
            resultAppointment = appointments.save(
                    appointment.rescheduledTo(current.requestedStartAt(), current.requestedEndAt()));
            decided = current.approved(adminUserId, now);
            historyReason = "Reprogramación aprobada";
        }

        RescheduleRequest saved = reschedules.save(decided);
        appointments.addHistory(new StatusHistoryEntry(current.appointmentId(), AppointmentStatus.APPROVED,
                adminUserId, ChangeSource.ADMIN, historyReason, now));
        return assembler.assemble(saved, resultAppointment);
    }
}
