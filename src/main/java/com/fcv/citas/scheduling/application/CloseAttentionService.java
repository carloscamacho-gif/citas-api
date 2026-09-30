package com.fcv.citas.scheduling.application;

import com.fcv.citas.professional.domain.exception.ProfessionalNotFoundException;
import com.fcv.citas.professional.domain.model.Professional;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import com.fcv.citas.scheduling.domain.exception.AppointmentAccessDeniedException;
import com.fcv.citas.scheduling.domain.exception.AppointmentNotFoundException;
import com.fcv.citas.scheduling.domain.exception.InvalidSchedulingException;
import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.AppointmentDetails;
import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.model.ChangeSource;
import com.fcv.citas.scheduling.domain.model.StatusHistoryEntry;
import com.fcv.citas.scheduling.domain.port.in.AttentionOutcome;
import com.fcv.citas.scheduling.domain.port.in.CloseAttentionUseCase;
import com.fcv.citas.scheduling.domain.port.out.AppointmentRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Implementa HU-021: el PROFESSIONAL cierra la atención de una cita propia ya iniciada, marcándola
 * COMPLETED o NO_SHOW (RF-17, RN-11). Solo el profesional dueño de la cita puede cerrarla (RF-16); el
 * cambio se registra en el historial con fuente PROFESSIONAL.
 */
@Service
public class CloseAttentionService implements CloseAttentionUseCase {

    private final ProfessionalRepositoryPort professionals;
    private final AppointmentRepositoryPort appointments;
    private final AppointmentDetailsAssembler assembler;
    private final Clock clock;

    public CloseAttentionService(ProfessionalRepositoryPort professionals, AppointmentRepositoryPort appointments,
                                 AppointmentDetailsAssembler assembler, Clock clock) {
        this.professionals = professionals;
        this.appointments = appointments;
        this.assembler = assembler;
        this.clock = clock;
    }

    @Override
    @Transactional
    public AppointmentDetails close(Long professionalUserId, Long appointmentId, AttentionOutcome outcome) {
        Professional professional = professionals.findByUserId(professionalUserId)
                .orElseThrow(() -> new ProfessionalNotFoundException(professionalUserId));
        // Fila bloqueada: cerrar no puede pisarse con una decisión/reprogramación simultánea.
        Appointment current = appointments.findByIdForUpdate(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
        // CA-03: solo el profesional dueño de la cita puede cerrarla.
        if (!current.professionalId().equals(professional.id())) {
            throw new AppointmentAccessDeniedException();
        }
        if (current.status() != AppointmentStatus.APPROVED) {
            throw new InvalidSchedulingException("Solo una cita aprobada puede cerrarse");
        }
        LocalDateTime now = LocalDateTime.now(clock);
        if (current.startAt().isAfter(now)) {
            throw new InvalidSchedulingException("No se puede cerrar una cita que aún no ha ocurrido");
        }

        AppointmentStatus target = outcome == AttentionOutcome.COMPLETED
                ? AppointmentStatus.COMPLETED : AppointmentStatus.NO_SHOW;
        Appointment saved = appointments.save(current.closedAs(target));
        appointments.addHistory(new StatusHistoryEntry(appointmentId, target, professionalUserId,
                ChangeSource.PROFESSIONAL, null, now));
        return assembler.assemble(saved);
    }
}
