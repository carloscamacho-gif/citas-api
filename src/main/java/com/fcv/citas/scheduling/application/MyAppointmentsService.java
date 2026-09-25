package com.fcv.citas.scheduling.application;

import com.fcv.citas.scheduling.domain.exception.InvalidSchedulingException;
import com.fcv.citas.scheduling.domain.model.AppointmentDetails;
import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.port.in.MyAppointmentsUseCase;
import com.fcv.citas.scheduling.domain.port.out.AppointmentRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/** Implementa HU-017: el USER consulta sus citas, filtrables por estado y fecha. */
@Service
public class MyAppointmentsService implements MyAppointmentsUseCase {

    private final AppointmentRepositoryPort appointments;
    private final AppointmentDetailsAssembler assembler;

    public MyAppointmentsService(AppointmentRepositoryPort appointments, AppointmentDetailsAssembler assembler) {
        this.appointments = appointments;
        this.assembler = assembler;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDetails> list(Long patientUserId, String status, LocalDate date) {
        AppointmentStatus parsed = parseStatus(status);
        return appointments.findByPatient(patientUserId, parsed, date).stream()
                .map(assembler::assemble)
                .toList();
    }

    private AppointmentStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return AppointmentStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidSchedulingException("Estado de cita inválido: " + status);
        }
    }
}
