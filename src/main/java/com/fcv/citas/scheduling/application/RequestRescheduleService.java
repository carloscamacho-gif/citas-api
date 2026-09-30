package com.fcv.citas.scheduling.application;

import com.fcv.citas.scheduling.domain.exception.AppointmentAccessDeniedException;
import com.fcv.citas.scheduling.domain.exception.AppointmentNotFoundException;
import com.fcv.citas.scheduling.domain.exception.InvalidSchedulingException;
import com.fcv.citas.scheduling.domain.exception.SchedulingConflictException;
import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.model.AvailabilitySlot;
import com.fcv.citas.scheduling.domain.model.ChangeSource;
import com.fcv.citas.scheduling.domain.model.RescheduleDetails;
import com.fcv.citas.scheduling.domain.model.RescheduleRequest;
import com.fcv.citas.scheduling.domain.model.RescheduleStatus;
import com.fcv.citas.scheduling.domain.model.StatusHistoryEntry;
import com.fcv.citas.scheduling.domain.port.in.RequestRescheduleUseCase;
import com.fcv.citas.scheduling.domain.port.in.RescheduleCommand;
import com.fcv.citas.scheduling.domain.port.out.AppointmentRepositoryPort;
import com.fcv.citas.scheduling.domain.port.out.RescheduleRepositoryPort;
import com.fcv.citas.scheduling.domain.port.out.SchedulingRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementa HU-019: el USER solicita reprogramar una cita aprobada y futura propia (RF-15, RN-10).
 *
 * <p>La nueva franja se retiene (sus slots se asignan a la misma cita) sin liberar la franja original:
 * la cita queda ocupando temporalmente ambas franjas hasta que ADMIN decida. La cita en sí no cambia de
 * estado ni de horario mientras la solicitud está PENDING.
 */
@Service
public class RequestRescheduleService implements RequestRescheduleUseCase {

    private static final int SLOT_MINUTES = 30;

    private final AppointmentRepositoryPort appointments;
    private final RescheduleRepositoryPort reschedules;
    private final SchedulingRepositoryPort scheduling;
    private final RescheduleDetailsAssembler assembler;
    private final Clock clock;

    public RequestRescheduleService(AppointmentRepositoryPort appointments, RescheduleRepositoryPort reschedules,
                                    SchedulingRepositoryPort scheduling, RescheduleDetailsAssembler assembler,
                                    Clock clock) {
        this.appointments = appointments;
        this.reschedules = reschedules;
        this.scheduling = scheduling;
        this.assembler = assembler;
        this.clock = clock;
    }

    @Override
    @Transactional
    public RescheduleDetails request(Long patientUserId, RescheduleCommand command) {
        // Fila bloqueada: solicitar reprogramación no puede pisarse con cancelar/decidir la misma cita.
        Appointment appointment = appointments.findByIdForUpdate(command.appointmentId())
                .orElseThrow(() -> new AppointmentNotFoundException(command.appointmentId()));
        if (!appointment.patientUserId().equals(patientUserId)) {
            throw new AppointmentAccessDeniedException();
        }
        if (appointment.status() != AppointmentStatus.APPROVED) {
            throw new InvalidSchedulingException("Solo una cita aprobada puede reprogramarse");
        }
        LocalDateTime now = LocalDateTime.now(clock);
        if (!appointment.startAt().isAfter(now)) {
            throw new InvalidSchedulingException("Solo se puede reprogramar una cita futura");
        }
        // CA-03: cambiar de profesional se trata como una cita nueva, no como reprogramación.
        if (command.professionalId() != null && !command.professionalId().equals(appointment.professionalId())) {
            throw new InvalidSchedulingException("No se puede cambiar de profesional al reprogramar; solicita una cita nueva");
        }
        if (command.requestedStartAt() == null || !command.requestedStartAt().isAfter(now)) {
            throw new InvalidSchedulingException("La nueva fecha/hora debe ser futura");
        }
        if (command.requestedStartAt().equals(appointment.startAt())) {
            throw new InvalidSchedulingException("La nueva fecha/hora debe ser distinta de la actual");
        }
        if (reschedules.existsPendingForAppointment(appointment.id())) {
            throw new SchedulingConflictException("La cita ya tiene una reprogramación pendiente de decisión");
        }

        int minutes = appointment.durationMinutes();
        int slotsNeeded = minutes / SLOT_MINUTES;
        Long locationId = command.locationId() != null ? command.locationId() : appointment.locationId();

        // Retiene la nueva franja sin liberar la original: los slots se bloquean y se asignan a la misma cita.
        List<AvailabilitySlot> locked = scheduling.lockRequiredSlots(
                appointment.professionalId(), locationId, command.requestedStartAt(), slotsNeeded);
        scheduling.assignSlots(locked.stream().map(AvailabilitySlot::id).toList(), appointment.id());

        RescheduleRequest saved = reschedules.save(new RescheduleRequest(null, appointment.id(), patientUserId,
                locationId, RescheduleStatus.PENDING, appointment.startAt(), appointment.endAt(),
                command.requestedStartAt(), command.requestedStartAt().plusMinutes(minutes), null, null, null));

        // T-03: la creación de la solicitud queda registrada en la auditoría de la cita (fuente USER).
        appointments.addHistory(new StatusHistoryEntry(appointment.id(), AppointmentStatus.APPROVED, patientUserId,
                ChangeSource.USER, "Reprogramación solicitada", now));

        return assembler.assemble(saved, appointment);
    }
}
