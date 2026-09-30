package com.fcv.citas.scheduling.application;

import com.fcv.citas.catalog.domain.model.Location;
import com.fcv.citas.catalog.domain.model.Specialty;
import com.fcv.citas.catalog.domain.port.out.LocationRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.SpecialtyRepositoryPort;
import com.fcv.citas.professional.domain.model.Professional;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import com.fcv.citas.scheduling.domain.exception.AppointmentNotFoundException;
import com.fcv.citas.scheduling.domain.exception.InvalidSchedulingException;
import com.fcv.citas.scheduling.domain.exception.SchedulingConflictException;
import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.AppointmentDetails;
import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.model.ChangeSource;
import com.fcv.citas.scheduling.domain.model.StatusHistoryEntry;
import com.fcv.citas.scheduling.domain.port.in.AppointmentDecision;
import com.fcv.citas.scheduling.domain.port.in.PendingAppointmentFilter;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** RF-12, RF-18, RN-03, RN-04, RN-09 — decisión administrativa de citas especializadas. */
class AppointmentDecisionServiceTest {

    private static final Long ADMIN_ID = 1L;
    private static final Long APPOINTMENT_ID = 500L;
    private static final LocalDateTime START = LocalDateTime.of(2026, 10, 1, 8, 0);

    private AppointmentRepositoryPort appointments;
    private SchedulingRepositoryPort scheduling;
    private AppointmentDecisionService service;

    @BeforeEach
    void setUp() {
        appointments = mock(AppointmentRepositoryPort.class);
        scheduling = mock(SchedulingRepositoryPort.class);
        ProfessionalRepositoryPort professionals = mock(ProfessionalRepositoryPort.class);
        SpecialtyRepositoryPort specialties = mock(SpecialtyRepositoryPort.class);
        LocationRepositoryPort locations = mock(LocationRepositoryPort.class);
        when(professionals.findById(7L)).thenReturn(Optional.of(new Professional(7L, 70L, "Laura", "Demo",
                "laura@example.test", "PRO-1", "LIC-1", true, List.of(2L), 2L, List.of(1L))));
        when(specialties.findById(2L)).thenReturn(Optional.of(new Specialty(2L, "CAR", "Cardiología", 60, false, true)));
        when(locations.findById(1L)).thenReturn(Optional.of(new Location(1L, "HIC", "Hospital Internacional", true)));
        Clock clock = Clock.fixed(Instant.parse("2026-09-25T15:00:00Z"), ZoneId.of("America/Bogota"));
        service = new AppointmentDecisionService(appointments, scheduling,
                new AppointmentDetailsAssembler(professionals, specialties, locations), clock);
        when(appointments.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Appointment appointment(AppointmentStatus status) {
        return new Appointment(APPOINTMENT_ID, 30L, 7L, 1L, 2L, status, "Control", null,
                START, START.plusMinutes(60), null, null);
    }

    private void givenPending(AppointmentStatus status) {
        when(appointments.findByIdForUpdate(APPOINTMENT_ID)).thenReturn(Optional.of(appointment(status)));
    }

    @Test
    void approvingKeepsTheSlotsAndAuditsTheAdminDecision() {
        givenPending(AppointmentStatus.REQUESTED);

        AppointmentDetails result = service.decide(ADMIN_ID, APPOINTMENT_ID, AppointmentDecision.APPROVE, null);

        assertThat(result.appointment().status()).isEqualTo(AppointmentStatus.APPROVED);
        assertThat(result.appointment().approvedByUserId()).isEqualTo(ADMIN_ID);
        verify(scheduling, never()).releaseSlots(anyLong());

        ArgumentCaptor<StatusHistoryEntry> history = ArgumentCaptor.forClass(StatusHistoryEntry.class);
        verify(appointments).addHistory(history.capture());
        assertThat(history.getValue().status()).isEqualTo(AppointmentStatus.APPROVED);
        assertThat(history.getValue().source()).isEqualTo(ChangeSource.ADMIN);
        assertThat(history.getValue().changedByUserId()).isEqualTo(ADMIN_ID);
    }

    @Test
    void rejectingRequiresAReason() {
        givenPending(AppointmentStatus.REQUESTED);

        assertThatThrownBy(() -> service.decide(ADMIN_ID, APPOINTMENT_ID, AppointmentDecision.REJECT, "  "))
                .isInstanceOf(InvalidSchedulingException.class)
                .hasMessageContaining("motivo");

        verify(scheduling, never()).releaseSlots(anyLong());
        verify(appointments, never()).save(any());
    }

    @Test
    void rejectingFreesTheSlotsAndStoresTheReason() {
        givenPending(AppointmentStatus.REQUESTED);

        AppointmentDetails result = service.decide(ADMIN_ID, APPOINTMENT_ID, AppointmentDecision.REJECT, "Sin cupo clínico");

        assertThat(result.appointment().status()).isEqualTo(AppointmentStatus.REJECTED);
        assertThat(result.appointment().rejectionReason()).isEqualTo("Sin cupo clínico");
        verify(scheduling).releaseSlots(APPOINTMENT_ID);

        ArgumentCaptor<StatusHistoryEntry> history = ArgumentCaptor.forClass(StatusHistoryEntry.class);
        verify(appointments).addHistory(history.capture());
        assertThat(history.getValue().status()).isEqualTo(AppointmentStatus.REJECTED);
        assertThat(history.getValue().reason()).isEqualTo("Sin cupo clínico");
        assertThat(history.getValue().source()).isEqualTo(ChangeSource.ADMIN);
    }

    @Test
    void aRepeatedDecisionIsAConflict() {
        givenPending(AppointmentStatus.APPROVED);

        assertThatThrownBy(() -> service.decide(ADMIN_ID, APPOINTMENT_ID, AppointmentDecision.APPROVE, null))
                .isInstanceOf(SchedulingConflictException.class);

        verify(appointments, never()).save(any());
        verify(appointments, never()).addHistory(any());
    }

    @Test
    void unknownAppointmentIsNotFound() {
        when(appointments.findByIdForUpdate(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.decide(ADMIN_ID, 999L, AppointmentDecision.APPROVE, null))
                .isInstanceOf(AppointmentNotFoundException.class);
    }

    @Test
    void pendingInboxOnlyListsRequestedAppointmentsWithNames() {
        when(appointments.findByStatus(AppointmentStatus.REQUESTED, PendingAppointmentFilter.none()))
                .thenReturn(List.of(appointment(AppointmentStatus.REQUESTED)));

        List<AppointmentDetails> inbox = service.pendingSpecialized(PendingAppointmentFilter.none());

        assertThat(inbox).hasSize(1);
        assertThat(inbox.get(0).professionalName()).isEqualTo("Laura Demo");
        assertThat(inbox.get(0).specialtyName()).isEqualTo("Cardiología");
        assertThat(inbox.get(0).locationName()).isEqualTo("Hospital Internacional");
    }
}
