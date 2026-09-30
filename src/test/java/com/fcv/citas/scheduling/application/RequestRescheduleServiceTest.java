package com.fcv.citas.scheduling.application;

import com.fcv.citas.auth.domain.port.out.UserRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.LocationRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.SpecialtyRepositoryPort;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import com.fcv.citas.scheduling.domain.exception.AppointmentAccessDeniedException;
import com.fcv.citas.scheduling.domain.exception.InvalidSchedulingException;
import com.fcv.citas.scheduling.domain.exception.SchedulingConflictException;
import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.model.AvailabilitySlot;
import com.fcv.citas.scheduling.domain.model.ChangeSource;
import com.fcv.citas.scheduling.domain.model.RescheduleDetails;
import com.fcv.citas.scheduling.domain.model.RescheduleStatus;
import com.fcv.citas.scheduling.domain.model.StatusHistoryEntry;
import com.fcv.citas.scheduling.domain.port.in.RescheduleCommand;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** HU-019 / RF-15, RN-10: solicitud de reprogramación de una cita aprobada y futura del propio USER. */
class RequestRescheduleServiceTest {

    private static final Long PATIENT = 30L;
    private static final Long PROFESSIONAL = 7L;
    private static final Long APPOINTMENT_ID = 500L;
    // "Ahora" fijo: 2026-09-25 10:00 (Bogotá).
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-09-25T15:00:00Z"), ZoneId.of("America/Bogota"));
    private static final LocalDateTime CURRENT = LocalDateTime.of(2026, 10, 6, 8, 0);
    private static final LocalDateTime NEW_SLOT = LocalDateTime.of(2026, 10, 7, 9, 0);
    private static final LocalDateTime PAST = LocalDateTime.of(2026, 9, 24, 8, 0);

    private AppointmentRepositoryPort appointments;
    private RescheduleRepositoryPort reschedules;
    private SchedulingRepositoryPort scheduling;
    private RequestRescheduleService service;

    @BeforeEach
    void setUp() {
        appointments = mock(AppointmentRepositoryPort.class);
        reschedules = mock(RescheduleRepositoryPort.class);
        scheduling = mock(SchedulingRepositoryPort.class);
        ProfessionalRepositoryPort professionals = mock(ProfessionalRepositoryPort.class);
        SpecialtyRepositoryPort specialties = mock(SpecialtyRepositoryPort.class);
        LocationRepositoryPort locations = mock(LocationRepositoryPort.class);
        UserRepositoryPort users = mock(UserRepositoryPort.class);
        service = new RequestRescheduleService(appointments, reschedules, scheduling,
                new RescheduleDetailsAssembler(professionals, specialties, locations, users), CLOCK);
        when(reschedules.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(scheduling.lockRequiredSlots(anyLong(), anyLong(), any(), anyInt()))
                .thenReturn(List.of(new AvailabilitySlot(1L, 1L, NEW_SLOT, NEW_SLOT.plusMinutes(30), null)));
    }

    private Appointment appointment(Long patient, Long professionalId, AppointmentStatus status, LocalDateTime start) {
        return new Appointment(APPOINTMENT_ID, patient, professionalId, 1L, 1L, status, null, null, start,
                start.plusMinutes(30), null, null);
    }

    private void given(Appointment a) {
        when(appointments.findByIdForUpdate(APPOINTMENT_ID)).thenReturn(Optional.of(a));
    }

    private RescheduleCommand command(LocalDateTime requestedStart) {
        return new RescheduleCommand(APPOINTMENT_ID, PROFESSIONAL, 1L, requestedStart);
    }

    @Test
    void validRequestRetainsNewSlotWithoutTouchingOriginalAndStaysPending() {
        given(appointment(PATIENT, PROFESSIONAL, AppointmentStatus.APPROVED, CURRENT));

        RescheduleDetails result = service.request(PATIENT, command(NEW_SLOT));

        assertThat(result.request().status()).isEqualTo(RescheduleStatus.PENDING);
        assertThat(result.request().previousStartAt()).isEqualTo(CURRENT);
        assertThat(result.request().requestedStartAt()).isEqualTo(NEW_SLOT);
        // La nueva franja se retiene asignando sus slots a la MISMA cita; la original nunca se libera.
        verify(scheduling).assignSlots(List.of(1L), APPOINTMENT_ID);
        verify(scheduling, never()).releaseSlots(anyLong());
        verify(appointments, never()).save(any());
        // Auditoría de la creación con fuente USER.
        ArgumentCaptor<StatusHistoryEntry> history = ArgumentCaptor.forClass(StatusHistoryEntry.class);
        verify(appointments).addHistory(history.capture());
        assertThat(history.getValue().source()).isEqualTo(ChangeSource.USER);
    }

    @Test
    void cannotRescheduleANonApprovedAppointment() {
        given(appointment(PATIENT, PROFESSIONAL, AppointmentStatus.REQUESTED, CURRENT));

        assertThatThrownBy(() -> service.request(PATIENT, command(NEW_SLOT)))
                .isInstanceOf(InvalidSchedulingException.class);
        verify(scheduling, never()).assignSlots(any(), anyLong());
    }

    @Test
    void cannotRescheduleAPastAppointment() {
        given(appointment(PATIENT, PROFESSIONAL, AppointmentStatus.APPROVED, PAST));

        assertThatThrownBy(() -> service.request(PATIENT, command(NEW_SLOT)))
                .isInstanceOf(InvalidSchedulingException.class).hasMessageContaining("futura");
    }

    @Test
    void cannotChangeProfessionalWhenRescheduling() {
        given(appointment(PATIENT, PROFESSIONAL, AppointmentStatus.APPROVED, CURRENT));

        RescheduleCommand changesProfessional = new RescheduleCommand(APPOINTMENT_ID, 999L, 1L, NEW_SLOT);
        assertThatThrownBy(() -> service.request(PATIENT, changesProfessional))
                .isInstanceOf(InvalidSchedulingException.class).hasMessageContaining("profesional");
        verify(scheduling, never()).assignSlots(any(), anyLong());
    }

    @Test
    void cannotRescheduleSomeoneElsesAppointment() {
        given(appointment(999L, PROFESSIONAL, AppointmentStatus.APPROVED, CURRENT));

        assertThatThrownBy(() -> service.request(PATIENT, command(NEW_SLOT)))
                .isInstanceOf(AppointmentAccessDeniedException.class);
    }

    @Test
    void cannotRequestASecondPendingRescheduleForTheSameAppointment() {
        given(appointment(PATIENT, PROFESSIONAL, AppointmentStatus.APPROVED, CURRENT));
        when(reschedules.existsPendingForAppointment(APPOINTMENT_ID)).thenReturn(true);

        assertThatThrownBy(() -> service.request(PATIENT, command(NEW_SLOT)))
                .isInstanceOf(SchedulingConflictException.class);
        verify(scheduling, never()).assignSlots(any(), eq(APPOINTMENT_ID));
    }
}
