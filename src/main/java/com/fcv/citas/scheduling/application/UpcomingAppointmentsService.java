package com.fcv.citas.scheduling.application;

import com.fcv.citas.scheduling.domain.model.AppointmentDetails;
import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.port.in.UpcomingAppointmentsUseCase;
import com.fcv.citas.scheduling.domain.port.out.AppointmentRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementa S5 (WF-001/WF-003): consulta de citas APPROVED en un rango de días. Si no se indica rango se toma
 * desde hoy hasta mañana (ventana de recordatorio por defecto). Consumida por las automatizaciones n8n.
 */
@Service
public class UpcomingAppointmentsService implements UpcomingAppointmentsUseCase {

    private final AppointmentRepositoryPort appointments;
    private final AppointmentDetailsAssembler assembler;
    private final Clock clock;

    public UpcomingAppointmentsService(AppointmentRepositoryPort appointments, AppointmentDetailsAssembler assembler,
                                       Clock clock) {
        this.appointments = appointments;
        this.assembler = assembler;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDetails> upcoming(LocalDate from, LocalDate to, Long locationId) {
        LocalDate today = LocalDate.now(clock);
        LocalDate start = from != null ? from : today;
        LocalDate end = to != null ? to : today.plusDays(1);
        return appointments.findByStatusInRange(AppointmentStatus.APPROVED, start, end, locationId)
                .stream().map(assembler::assemble).toList();
    }
}
