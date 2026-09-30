package com.fcv.citas.scheduling.application;

import com.fcv.citas.professional.domain.model.Professional;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import com.fcv.citas.scheduling.domain.exception.AppointmentAccessDeniedException;
import com.fcv.citas.scheduling.domain.exception.AppointmentNotFoundException;
import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.StatusHistoryEntry;
import com.fcv.citas.scheduling.domain.port.in.AppointmentHistoryUseCase;
import com.fcv.citas.scheduling.domain.port.out.AppointmentRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/** Implementa la consulta de auditoría de HU-024 con control de ownership por rol. */
@Service
public class AppointmentHistoryService implements AppointmentHistoryUseCase {

    private final AppointmentRepositoryPort appointments;
    private final ProfessionalRepositoryPort professionals;

    public AppointmentHistoryService(AppointmentRepositoryPort appointments, ProfessionalRepositoryPort professionals) {
        this.appointments = appointments;
        this.professionals = professionals;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatusHistoryEntry> history(Long requesterUserId, Collection<String> roles, Long appointmentId) {
        Appointment appointment = appointments.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
        if (!canSee(requesterUserId, roles, appointment)) {
            throw new AppointmentAccessDeniedException();
        }
        return appointments.history(appointmentId);
    }

    private boolean canSee(Long requesterUserId, Collection<String> roles, Appointment appointment) {
        if (roles.contains("ADMIN")) {
            return true;
        }
        if (roles.contains("PROFESSIONAL")) {
            return professionals.findByUserId(requesterUserId)
                    .map(Professional::id)
                    .filter(appointment.professionalId()::equals)
                    .isPresent();
        }
        return appointment.patientUserId().equals(requesterUserId);
    }
}
