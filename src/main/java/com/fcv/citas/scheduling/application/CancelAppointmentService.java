package com.fcv.citas.scheduling.application;

import com.fcv.citas.scheduling.domain.exception.AppointmentAccessDeniedException;
import com.fcv.citas.scheduling.domain.exception.AppointmentNotFoundException;
import com.fcv.citas.scheduling.domain.exception.InvalidSchedulingException;
import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.AppointmentDetails;
import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.model.ChangeSource;
import com.fcv.citas.scheduling.domain.model.StatusHistoryEntry;
import com.fcv.citas.scheduling.domain.port.in.CancelAppointmentUseCase;
import com.fcv.citas.scheduling.domain.port.out.AppointmentRepositoryPort;
import com.fcv.citas.scheduling.domain.port.out.SchedulingRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

/** Implementa HU-018: el USER cancela una cita futura no terminal propia (RF-14, RN-09). */
@Service
public class CancelAppointmentService implements CancelAppointmentUseCase {

    private final AppointmentRepositoryPort appointments;
    private final SchedulingRepositoryPort scheduling;
    private final AppointmentDetailsAssembler assembler;
    private final Clock clock;

    public CancelAppointmentService(AppointmentRepositoryPort appointments, SchedulingRepositoryPort scheduling,
                                    AppointmentDetailsAssembler assembler, Clock clock) {
        this.appointments = appointments;
        this.scheduling = scheduling;
        this.assembler = assembler;
        this.clock = clock;
    }

    @Override
    @Transactional
    public AppointmentDetails cancel(Long patientUserId, Long appointmentId) {
        // Fila bloqueada: cancelar y decidir/reprogramar no pueden pisarse.
        Appointment current = appointments.findByIdForUpdate(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
        if (!current.patientUserId().equals(patientUserId)) {
            throw new AppointmentAccessDeniedException();
        }
        if (current.isTerminal()) {
            throw new InvalidSchedulingException("La cita ya está en un estado que no permite cancelación");
        }
        LocalDateTime now = LocalDateTime.now(clock);
        if (!current.startAt().isAfter(now)) {
            throw new InvalidSchedulingException("Solo se puede cancelar una cita futura");
        }

        Appointment cancelled = appointments.save(current.cancelled());
        scheduling.releaseSlots(appointmentId);
        appointments.addHistory(new StatusHistoryEntry(appointmentId, AppointmentStatus.CANCELLED, patientUserId,
                ChangeSource.USER, null, now));
        return assembler.assemble(cancelled);
    }
}
