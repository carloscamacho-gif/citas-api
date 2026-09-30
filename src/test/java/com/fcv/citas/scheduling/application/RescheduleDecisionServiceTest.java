package com.fcv.citas.scheduling.application;

import com.fcv.citas.auth.domain.port.out.UserRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.LocationRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.SpecialtyRepositoryPort;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import com.fcv.citas.scheduling.domain.exception.InvalidSchedulingException;
import com.fcv.citas.scheduling.domain.exception.RescheduleRequestNotFoundException;
import com.fcv.citas.scheduling.domain.exception.SchedulingConflictException;
import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.model.RescheduleDetails;
import com.fcv.citas.scheduling.domain.model.RescheduleRequest;
import com.fcv.citas.scheduling.domain.model.RescheduleStatus;
import com.fcv.citas.scheduling.domain.port.in.AppointmentDecision;
import com.fcv.citas.scheduling.domain.port.out.AppointmentRepositoryPort;
import com.fcv.citas.scheduling.domain.port.out.RescheduleRepositoryPort;
import com.fcv.citas.scheduling.domain.port.out.SchedulingRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** HU-023 / RF-15, RN-04, RN-09, RN-10: decisión ADMIN sobre una reprogramación PENDING. */
class RescheduleDecisionServiceTest {

    private static final Long ADMIN = 1L;
    private static final Long APPOINTMENT_ID = 500L;
    private static final Long RESCHEDULE_ID = 800L;
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-09-25T15:00:00Z"), ZoneId.of("America/Bogota"));
    private static final LocalDateTime OLD_START = LocalDateTime.of(2026, 10, 6, 8, 0);
    private static final LocalDateTime OLD_END = OLD_START.plusMinutes(30);
    private static final LocalDateTime NEW_START = LocalDateTime.of(2026, 10, 7, 9, 0);
    private static final LocalDateTime NEW_END = NEW_START.plusMinutes(30);

    private RescheduleRepositoryPort reschedules;
    private AppointmentRepositoryPort appointments;
    private SchedulingRepositoryPort scheduling;
    private RescheduleDecisionService service;

    @BeforeEach
    void setUp() {
        reschedules = mock(RescheduleRepositoryPort.class);
        appointments = mock(AppointmentRepositoryPort.class);
        scheduling = mock(SchedulingRepositoryPort.class);
        ProfessionalRepositoryPort professionals = mock(ProfessionalRepositoryPort.class);
        SpecialtyRepositoryPort specialties = mock(SpecialtyRepositoryPort.class);
        LocationRepositoryPort locations = mock(LocationRepositoryPort.class);
        UserRepositoryPort users = mock(UserRepositoryPort.class);
        service = new RescheduleDecisionService(reschedules, appointments, scheduling,
                new RescheduleDetailsAssembler(professionals, specialties, locations, users), CLOCK);
        when(reschedules.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(appointments.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private RescheduleRequest pending() {
        return new RescheduleRequest(RESCHEDULE_ID, APPOINTMENT_ID, 30L, 1L, RescheduleStatus.PENDING,
                OLD_START, OLD_END, NEW_START, NEW_END, null, null, null);
    }

    private Appointment appointment() {
        return new Appointment(APPOINTMENT_ID, 30L, 7L, 1L, 1L, AppointmentStatus.APPROVED, null, null,
                OLD_START, OLD_END, ADMIN, OLD_START.minusDays(1));
    }

    private void givenPending() {
        when(reschedules.findByIdForUpdate(RESCHEDULE_ID)).thenReturn(Optional.of(pending()));
        when(appointments.findByIdForUpdate(APPOINTMENT_ID)).thenReturn(Optional.of(appointment()));
    }

    @Test
    void approvingReleasesOldFranjaAndMovesTheAppointmentToTheNewOne() {
        givenPending();

        RescheduleDetails result = service.decide(ADMIN, RESCHEDULE_ID, AppointmentDecision.APPROVE, null);

        assertThat(result.request().status()).isEqualTo(RescheduleStatus.APPROVED);
        // Libera SOLO la franja antigua; la nueva ya estaba retenida sobre la cita y se conserva.
        verify(scheduling).releaseSlotsInRange(APPOINTMENT_ID, OLD_START, OLD_END);
        verify(scheduling, never()).releaseSlotsInRange(APPOINTMENT_ID, NEW_START, NEW_END);
        ArgumentCaptor<Appointment> saved = ArgumentCaptor.forClass(Appointment.class);
        verify(appointments).save(saved.capture());
        assertThat(saved.getValue().startAt()).isEqualTo(NEW_START);
        assertThat(saved.getValue().endAt()).isEqualTo(NEW_END);
        assertThat(saved.getValue().status()).isEqualTo(AppointmentStatus.APPROVED);
    }

    @Test
    void rejectingReleasesTheNewProvisionalFranjaAndKeepsTheAppointment() {
        givenPending();

        RescheduleDetails result = service.decide(ADMIN, RESCHEDULE_ID, AppointmentDecision.REJECT, "Sin agenda ese día");

        assertThat(result.request().status()).isEqualTo(RescheduleStatus.REJECTED);
        assertThat(result.request().decisionReason()).isEqualTo("Sin agenda ese día");
        // Libera SOLO la franja nueva provisional; la cita original no se toca.
        verify(scheduling).releaseSlotsInRange(APPOINTMENT_ID, NEW_START, NEW_END);
        verify(scheduling, never()).releaseSlotsInRange(APPOINTMENT_ID, OLD_START, OLD_END);
        verify(appointments, never()).save(any(Appointment.class));
    }

    @Test
    void rejectingWithoutAReasonIsInvalid() {
        givenPending();

        assertThatThrownBy(() -> service.decide(ADMIN, RESCHEDULE_ID, AppointmentDecision.REJECT, "  "))
                .isInstanceOf(InvalidSchedulingException.class);
        verify(scheduling, never()).releaseSlotsInRange(anyLong(), any(), any());
    }

    @Test
    void cannotDecideARescheduleThatIsNoLongerPending() {
        RescheduleRequest decided = pending().approved(ADMIN, LocalDateTime.now(CLOCK));
        when(reschedules.findByIdForUpdate(RESCHEDULE_ID)).thenReturn(Optional.of(decided));

        assertThatThrownBy(() -> service.decide(ADMIN, RESCHEDULE_ID, AppointmentDecision.APPROVE, null))
                .isInstanceOf(SchedulingConflictException.class);
    }

    @Test
    void unknownRescheduleIsNotFound() {
        when(reschedules.findByIdForUpdate(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.decide(ADMIN, 404L, AppointmentDecision.APPROVE, null))
                .isInstanceOf(RescheduleRequestNotFoundException.class);
    }
}
