package com.fcv.citas.scheduling.application;

import com.fcv.citas.catalog.domain.model.Location;
import com.fcv.citas.catalog.domain.model.Specialty;
import com.fcv.citas.catalog.domain.port.out.LocationRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.SpecialtyRepositoryPort;
import com.fcv.citas.professional.domain.model.Professional;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import com.fcv.citas.scheduling.domain.exception.InvalidSchedulingException;
import com.fcv.citas.scheduling.domain.exception.SchedulingConflictException;
import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.AppointmentDetails;
import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.model.AvailabilitySlot;
import com.fcv.citas.scheduling.domain.model.ChangeSource;
import com.fcv.citas.scheduling.domain.model.StatusHistoryEntry;
import com.fcv.citas.scheduling.domain.port.in.BookAppointmentCommand;
import com.fcv.citas.scheduling.domain.port.out.AppointmentRepositoryPort;
import com.fcv.citas.scheduling.domain.port.out.SchedulingRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** RF-11, RF-12, RN-01, RN-02, RN-05, RN-06, RN-08 — agendamiento de cita general y especializada. */
class BookAppointmentServiceTest {

    private static final Long PATIENT_ID = 30L;
    private static final LocalDateTime START = LocalDateTime.of(2026, 10, 1, 8, 0);

    private final Specialty general = new Specialty(1L, "MG", "Medicina General", 30, true, true);
    private final Specialty specialized60 = new Specialty(2L, "CAR", "Cardiología", 60, false, true);
    private final Location hic = new Location(1L, "HIC", "Hospital Internacional de Colombia", true);
    private final Professional professional = new Professional(7L, 70L, "Laura", "Demo", "laura@example.test",
            "PRO-1", "LIC-1", true, List.of(1L, 2L), 1L, List.of(1L));

    private ProfessionalRepositoryPort professionals;
    private SpecialtyRepositoryPort specialties;
    private LocationRepositoryPort locations;
    private SchedulingRepositoryPort scheduling;
    private AppointmentRepositoryPort appointments;
    private BookAppointmentService service;

    @BeforeEach
    void setUp() {
        professionals = mock(ProfessionalRepositoryPort.class);
        specialties = mock(SpecialtyRepositoryPort.class);
        locations = mock(LocationRepositoryPort.class);
        scheduling = mock(SchedulingRepositoryPort.class);
        appointments = mock(AppointmentRepositoryPort.class);
        // "Ahora" fijo: 2026-09-25 10:00 en America/Bogota.
        Clock clock = Clock.fixed(Instant.parse("2026-09-25T15:00:00Z"), ZoneId.of("America/Bogota"));
        var assembler = new AppointmentDetailsAssembler(professionals, specialties, locations);
        service = new BookAppointmentService(professionals, specialties, locations, scheduling, appointments,
                assembler, clock);

        when(specialties.findById(1L)).thenReturn(Optional.of(general));
        when(specialties.findById(2L)).thenReturn(Optional.of(specialized60));
        when(professionals.findById(7L)).thenReturn(Optional.of(professional));
        when(locations.findById(1L)).thenReturn(Optional.of(hic));
        when(appointments.save(any(Appointment.class))).thenAnswer(inv -> withId(inv.getArgument(0), 500L));
    }

    private static Appointment withId(Appointment a, Long id) {
        return new Appointment(id, a.patientUserId(), a.professionalId(), a.locationId(), a.specialtyId(),
                a.status(), a.reason(), a.rejectionReason(), a.startAt(), a.endAt(),
                a.approvedByUserId(), a.approvedAt());
    }

    private static AvailabilitySlot slot(long id, LocalDateTime start) {
        return new AvailabilitySlot(id, 5L, start, start.plusMinutes(30), null);
    }

    private BookAppointmentCommand command(long specialtyId, LocalDateTime start) {
        return new BookAppointmentCommand(7L, 1L, specialtyId, start, "Control");
    }

    @Test
    void generalAppointmentIsApprovedAutomaticallyWithSystemAuditAndOneSlot() {
        when(scheduling.lockRequiredSlots(7L, 1L, START, 1)).thenReturn(List.of(slot(101L, START)));

        AppointmentDetails result = service.book(PATIENT_ID, command(1L, START));

        assertThat(result.appointment().status()).isEqualTo(AppointmentStatus.APPROVED);
        assertThat(result.appointment().durationMinutes()).isEqualTo(30);
        assertThat(result.appointment().endAt()).isEqualTo(START.plusMinutes(30));
        assertThat(result.professionalName()).isEqualTo("Laura Demo");
        assertThat(result.specialtyName()).isEqualTo("Medicina General");
        verify(scheduling).assignSlots(List.of(101L), 500L);

        ArgumentCaptor<StatusHistoryEntry> history = ArgumentCaptor.forClass(StatusHistoryEntry.class);
        verify(appointments).addHistory(history.capture());
        assertThat(history.getValue().status()).isEqualTo(AppointmentStatus.APPROVED);
        assertThat(history.getValue().source()).isEqualTo(ChangeSource.SYSTEM);
        assertThat(history.getValue().changedByUserId()).isEqualTo(PATIENT_ID);
    }

    @Test
    void specializedAppointmentOf60MinutesLocksTwoConsecutiveSlotsAndWaitsForAdmin() {
        LocalDateTime second = START.plusMinutes(30);
        when(scheduling.lockRequiredSlots(7L, 1L, START, 2))
                .thenReturn(List.of(slot(101L, START), slot(102L, second)));

        AppointmentDetails result = service.book(PATIENT_ID, command(2L, START));

        assertThat(result.appointment().status()).isEqualTo(AppointmentStatus.REQUESTED);
        assertThat(result.appointment().durationMinutes()).isEqualTo(60);
        assertThat(result.appointment().endAt()).isEqualTo(START.plusMinutes(60));
        verify(scheduling).assignSlots(List.of(101L, 102L), 500L);

        ArgumentCaptor<StatusHistoryEntry> history = ArgumentCaptor.forClass(StatusHistoryEntry.class);
        verify(appointments).addHistory(history.capture());
        assertThat(history.getValue().status()).isEqualTo(AppointmentStatus.REQUESTED);
        assertThat(history.getValue().source()).isEqualTo(ChangeSource.USER);
    }

    @Test
    void rejectsAppointmentsInThePast() {
        LocalDateTime past = LocalDateTime.of(2026, 9, 25, 9, 0);

        assertThatThrownBy(() -> service.book(PATIENT_ID, command(1L, past)))
                .isInstanceOf(InvalidSchedulingException.class)
                .hasMessageContaining("pasado");

        verify(scheduling, never()).lockRequiredSlots(anyLong(), anyLong(), any(), anyInt());
        verify(appointments, never()).save(any());
    }

    @Test
    void rejectsInactiveSpecialty() {
        when(specialties.findById(1L)).thenReturn(Optional.of(new Specialty(1L, "MG", "Medicina General", 30, true, false)));

        assertThatThrownBy(() -> service.book(PATIENT_ID, command(1L, START)))
                .isInstanceOf(InvalidSchedulingException.class);

        verify(appointments, never()).save(any());
    }

    @Test
    void rejectsProfessionalNotAssignedToTheSpecialtyOrLocation() {
        Professional otherLocation = new Professional(7L, 70L, "Laura", "Demo", "laura@example.test",
                "PRO-1", "LIC-1", true, List.of(1L, 2L), 1L, List.of(2L));
        when(professionals.findById(7L)).thenReturn(Optional.of(otherLocation));

        assertThatThrownBy(() -> service.book(PATIENT_ID, command(1L, START)))
                .isInstanceOf(InvalidSchedulingException.class);

        verify(scheduling, never()).lockRequiredSlots(anyLong(), anyLong(), any(), anyInt());
    }

    @Test
    void rejectsInactiveProfessional() {
        when(professionals.findById(7L)).thenReturn(Optional.of(professional.withActive(false)));

        assertThatThrownBy(() -> service.book(PATIENT_ID, command(1L, START)))
                .isInstanceOf(InvalidSchedulingException.class);

        verify(appointments, never()).save(any());
    }

    @Test
    void whenTheSlotsAreNoLongerFreeNothingIsCreated() {
        when(scheduling.lockRequiredSlots(7L, 1L, START, 1))
                .thenThrow(new SchedulingConflictException("El horario dejó de estar disponible"));

        assertThatThrownBy(() -> service.book(PATIENT_ID, command(1L, START)))
                .isInstanceOf(SchedulingConflictException.class);

        verify(appointments, never()).save(any());
        verify(scheduling, never()).assignSlots(any(), anyLong());
        verify(appointments, never()).addHistory(any());
    }

    /** RN-01: el segundo intento sobre el mismo horario se rechaza y no crea una segunda cita. */
    @Test
    void doubleBookingOfTheSameSlotIsRejected() {
        when(scheduling.lockRequiredSlots(7L, 1L, START, 1))
                .thenReturn(List.of(slot(101L, START)))
                .thenThrow(new SchedulingConflictException("El horario dejó de estar disponible"));

        AppointmentDetails first = service.book(PATIENT_ID, command(1L, START));

        assertThat(first.appointment().id()).isEqualTo(500L);
        assertThatThrownBy(() -> service.book(31L, command(1L, START)))
                .isInstanceOf(SchedulingConflictException.class);
        verify(appointments, times(1)).save(any());
        verify(scheduling, times(1)).assignSlots(any(), anyLong());
    }
}
