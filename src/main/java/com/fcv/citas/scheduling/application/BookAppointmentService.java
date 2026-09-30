package com.fcv.citas.scheduling.application;

import com.fcv.citas.catalog.domain.exception.SpecialtyNotFoundException;
import com.fcv.citas.catalog.domain.model.Specialty;
import com.fcv.citas.catalog.domain.port.out.LocationRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.SpecialtyRepositoryPort;
import com.fcv.citas.professional.domain.exception.ProfessionalNotFoundException;
import com.fcv.citas.professional.domain.model.Professional;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import com.fcv.citas.scheduling.domain.exception.InvalidSchedulingException;
import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.AppointmentDetails;
import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.model.AvailabilitySlot;
import com.fcv.citas.scheduling.domain.model.ChangeSource;
import com.fcv.citas.scheduling.domain.model.StatusHistoryEntry;
import com.fcv.citas.scheduling.domain.port.in.BookAppointmentCommand;
import com.fcv.citas.scheduling.domain.port.in.BookAppointmentUseCase;
import com.fcv.citas.scheduling.domain.port.out.AppointmentRepositoryPort;
import com.fcv.citas.scheduling.domain.port.out.SchedulingRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementa HU-015 (cita general, auto-aprobada) y HU-016 (cita especializada, pendiente de ADMIN).
 *
 * <p>Todo el método es una única transacción: el bloqueo de filas de los slots ({@code lockRequiredSlots},
 * {@code SELECT ... FOR UPDATE}) se mantiene hasta asignar los slots y guardar la cita, de modo que dos
 * solicitudes simultáneas sobre el mismo horario no pueden ganar ambas (RN-01).
 */
@Service
public class BookAppointmentService implements BookAppointmentUseCase {

    private static final int SLOT_MINUTES = 30;

    private final ProfessionalRepositoryPort professionals;
    private final SpecialtyRepositoryPort specialties;
    private final LocationRepositoryPort locations;
    private final SchedulingRepositoryPort scheduling;
    private final AppointmentRepositoryPort appointments;
    private final AppointmentDetailsAssembler assembler;
    private final Clock clock;

    public BookAppointmentService(ProfessionalRepositoryPort professionals, SpecialtyRepositoryPort specialties,
                                  LocationRepositoryPort locations, SchedulingRepositoryPort scheduling,
                                  AppointmentRepositoryPort appointments, AppointmentDetailsAssembler assembler,
                                  Clock clock) {
        this.professionals = professionals;
        this.specialties = specialties;
        this.locations = locations;
        this.scheduling = scheduling;
        this.appointments = appointments;
        this.assembler = assembler;
        this.clock = clock;
    }

    @Override
    @Transactional
    public AppointmentDetails book(Long patientUserId, BookAppointmentCommand command) {
        Specialty specialty = specialties.findById(command.specialtyId())
                .orElseThrow(() -> new SpecialtyNotFoundException(command.specialtyId()));
        if (!specialty.isActive()) {
            throw new InvalidSchedulingException("La especialidad está inactiva");
        }
        Professional professional = professionals.findById(command.professionalId())
                .orElseThrow(() -> new ProfessionalNotFoundException(command.professionalId()));
        if (!professional.supports(command.specialtyId(), command.locationId())) {
            throw new InvalidSchedulingException(
                    "El profesional está inactivo o no atiende esa especialidad en esa sede");
        }
        if (command.locationId() == null || locations.findById(command.locationId()).filter(l -> l.active()).isEmpty()) {
            throw new InvalidSchedulingException("La sede no existe o está inactiva");
        }
        LocalDateTime now = LocalDateTime.now(clock);
        if (command.startAt() == null || !command.startAt().isAfter(now)) {
            throw new InvalidSchedulingException("No se permiten citas en el pasado");
        }

        int slotsNeeded = specialty.getDurationMinutes() / SLOT_MINUTES;
        List<AvailabilitySlot> locked = scheduling.lockRequiredSlots(
                professional.id(), command.locationId(), command.startAt(), slotsNeeded);

        AppointmentStatus status = specialty.isGeneral() ? AppointmentStatus.APPROVED : AppointmentStatus.REQUESTED;
        Appointment saved = appointments.save(new Appointment(null, patientUserId, professional.id(),
                command.locationId(), specialty.getId(), status, command.reason(), null,
                command.startAt(), command.startAt().plusMinutes(specialty.getDurationMinutes()), null, null));

        scheduling.assignSlots(locked.stream().map(AvailabilitySlot::id).toList(), saved.id());

        // La aprobación automática de una cita general la decide el sistema, a nombre del paciente (RF-19).
        ChangeSource source = specialty.isGeneral() ? ChangeSource.SYSTEM : ChangeSource.USER;
        appointments.addHistory(new StatusHistoryEntry(saved.id(), status, patientUserId, source, null, now));

        return assembler.assemble(saved);
    }
}
