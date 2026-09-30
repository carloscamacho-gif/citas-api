package com.fcv.citas.scheduling.application;

import com.fcv.citas.professional.domain.exception.ProfessionalNotFoundException;
import com.fcv.citas.professional.domain.model.Professional;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import com.fcv.citas.scheduling.domain.model.AppointmentDetails;
import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.port.in.ProfessionalAgendaUseCase;
import com.fcv.citas.scheduling.domain.port.out.AppointmentRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementa HU-020: agenda de solo lectura del PROFESSIONAL autenticado (RF-16).
 *
 * <p>El identificador autenticado es el id de usuario; se resuelve al profesional correspondiente y la
 * consulta se acota siempre a <em>ese</em> profesional, de modo que nunca puede ver citas de otro.
 */
@Service
public class ProfessionalAgendaService implements ProfessionalAgendaUseCase {

    private final ProfessionalRepositoryPort professionals;
    private final AppointmentRepositoryPort appointments;
    private final AppointmentDetailsAssembler assembler;

    public ProfessionalAgendaService(ProfessionalRepositoryPort professionals, AppointmentRepositoryPort appointments,
                                     AppointmentDetailsAssembler assembler) {
        this.professionals = professionals;
        this.appointments = appointments;
        this.assembler = assembler;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDetails> agenda(Long professionalUserId, LocalDate from, LocalDate to, Long locationId) {
        Professional professional = professionals.findByUserId(professionalUserId)
                .orElseThrow(() -> new ProfessionalNotFoundException(professionalUserId));
        return appointments.findByProfessional(professional.id(), AppointmentStatus.APPROVED, from, to, locationId)
                .stream().map(assembler::assemble).toList();
    }
}
