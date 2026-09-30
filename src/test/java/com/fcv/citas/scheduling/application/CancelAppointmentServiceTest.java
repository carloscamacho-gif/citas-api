package com.fcv.citas.scheduling.application;

import com.fcv.citas.catalog.domain.port.out.LocationRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.SpecialtyRepositoryPort;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import com.fcv.citas.scheduling.domain.exception.AppointmentAccessDeniedException;
import com.fcv.citas.scheduling.domain.exception.AppointmentNotFoundException;
import com.fcv.citas.scheduling.domain.exception.InvalidSchedulingException;
import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.AppointmentDetails;
import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.model.ChangeSource;
import com.fcv.citas.scheduling.domain.model.StatusHistoryEntry;
import com.fcv.citas.scheduling.domain.port.out.AppointmentRepositoryPort;
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

/** HU-018 / RF-14, RN-09: cancelación de una cita futura no terminal del propio USER. */
class CancelAppointmentServiceTest {

    private static final Long PATIENT = 30L;
    private static final Long APPOINTMENT_ID = 500L;
    // "Ahora" fijo: 2026-09-25 10:00 (Bogotá).
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-09-25T15:00:00Z"), ZoneId.of("America/Bogota"));
    private static final LocalDateTime FUTURE = LocalDateTime.of(2026, 10, 6, 8, 0);
    private static final LocalDateTime PAST = LocalDateTime.of(2026, 9, 25, 8, 0);

    private AppointmentRepositoryPort appointments;
    private SchedulingRepositoryPort scheduling;
    private CancelAppointmentService service;

    @BeforeEach
    void setUp() {
        appointments = mock(AppointmentRepositoryPort.class);
        scheduling = mock(SchedulingRepositoryPort.class);
        ProfessionalRepositoryPort professionals = mock(ProfessionalRepositoryPort.class);
        SpecialtyRepositoryPort specialties = mock(SpecialtyRepositoryPort.class);
        LocationRepositoryPort locations = mock(LocationRepositoryPort.class);
        service = new CancelAppointmentService(appointments, scheduling,
                new AppointmentDetailsAssembler(professionals, specialties, locations), CLOCK);
        when(appointments.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Appointment appointment(Long patient, AppointmentStatus status, LocalDateTime start) {
        return new Appointment(APPOINTMENT_ID, patient, 7L, 1L, 1L, status, null, null, start,
                start.plusMinutes(30), null, null);
    }

    private void given(Appointment a) {
        when(appointments.findByIdForUpdate(APPOINTMENT_ID)).thenReturn(Optional.of(a));
    }

    @Test
    void cancellingAFutureApprovedAppointmentReleasesSlotsAndAudits() {
        given(appointment(PATIENT, AppointmentStatus.APPROVED, FUTURE));

        AppointmentDetails result = service.cancel(PATIENT, APPOINTMENT_ID);

        assertThat(result.appointment().status()).isEqualTo(AppointmentStatus.CANCELLED);
        verify(scheduling).releaseSlots(APPOINTMENT_ID);
        ArgumentCaptor<StatusHistoryEntry> history = ArgumentCaptor.forClass(StatusHistoryEntry.class);
        verify(appointments).addHistory(history.capture());
        assertThat(history.getValue().status()).isEqualTo(AppointmentStatus.CANCELLED);
        assertThat(history.getValue().source()).isEqualTo(ChangeSource.USER);
        assertThat(history.getValue().changedByUserId()).isEqualTo(PATIENT);
    }

    @Test
    void aRequestedAppointmentCanAlsoBeCancelled() {
        given(appointment(PATIENT, AppointmentStatus.REQUESTED, FUTURE));

        assertThat(service.cancel(PATIENT, APPOINTMENT_ID).appointment().status())
                .isEqualTo(AppointmentStatus.CANCELLED);
        verify(scheduling).releaseSlots(APPOINTMENT_ID);
    }

    @Test
    void cannotCancelSomeoneElsesAppointment() {
        given(appointment(999L, AppointmentStatus.APPROVED, FUTURE));

        assertThatThrownBy(() -> service.cancel(PATIENT, APPOINTMENT_ID))
                .isInstanceOf(AppointmentAccessDeniedException.class);
        verify(appointments, never()).save(any());
        verify(scheduling, never()).releaseSlots(anyLong());
    }

    @Test
    void cannotCancelAPastAppointment() {
        given(appointment(PATIENT, AppointmentStatus.APPROVED, PAST));

        assertThatThrownBy(() -> service.cancel(PATIENT, APPOINTMENT_ID))
                .isInstanceOf(InvalidSchedulingException.class).hasMessageContaining("futura");
        verify(appointments, never()).save(any());
    }

    @Test
    void cannotCancelATerminalAppointment() {
        for (AppointmentStatus terminal : new AppointmentStatus[]{AppointmentStatus.CANCELLED,
                AppointmentStatus.REJECTED, AppointmentStatus.COMPLETED, AppointmentStatus.NO_SHOW}) {
            given(appointment(PATIENT, terminal, FUTURE));
            assertThatThrownBy(() -> service.cancel(PATIENT, APPOINTMENT_ID))
                    .isInstanceOf(InvalidSchedulingException.class);
        }
        verify(scheduling, never()).releaseSlots(anyLong());
    }

    @Test
    void unknownAppointmentIsNotFound() {
        when(appointments.findByIdForUpdate(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancel(PATIENT, 404L)).isInstanceOf(AppointmentNotFoundException.class);
    }
}
