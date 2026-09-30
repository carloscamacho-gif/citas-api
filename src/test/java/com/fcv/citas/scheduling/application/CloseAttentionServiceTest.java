package com.fcv.citas.scheduling.application;

import com.fcv.citas.catalog.domain.port.out.LocationRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.SpecialtyRepositoryPort;
import com.fcv.citas.professional.domain.exception.ProfessionalNotFoundException;
import com.fcv.citas.professional.domain.model.Professional;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import com.fcv.citas.scheduling.domain.exception.AppointmentAccessDeniedException;
import com.fcv.citas.scheduling.domain.exception.InvalidSchedulingException;
import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.AppointmentDetails;
import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.model.ChangeSource;
import com.fcv.citas.scheduling.domain.model.StatusHistoryEntry;
import com.fcv.citas.scheduling.domain.port.in.AttentionOutcome;
import com.fcv.citas.scheduling.domain.port.out.AppointmentRepositoryPort;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** HU-021 / RF-17, RN-11: cierre de atención (COMPLETED/NO_SHOW) por el profesional dueño de la cita. */
class CloseAttentionServiceTest {

    private static final Long PRO_USER = 7L;
    private static final Long PRO_ID = 1L;
    private static final Long APPOINTMENT_ID = 500L;
    // "Ahora" fijo: 2026-09-25 10:00 (Bogotá).
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-09-25T15:00:00Z"), ZoneId.of("America/Bogota"));
    private static final LocalDateTime PAST = LocalDateTime.of(2026, 9, 25, 8, 0);
    private static final LocalDateTime FUTURE = LocalDateTime.of(2026, 10, 6, 8, 0);

    private ProfessionalRepositoryPort professionals;
    private AppointmentRepositoryPort appointments;
    private CloseAttentionService service;

    @BeforeEach
    void setUp() {
        professionals = mock(ProfessionalRepositoryPort.class);
        appointments = mock(AppointmentRepositoryPort.class);
        SpecialtyRepositoryPort specialties = mock(SpecialtyRepositoryPort.class);
        LocationRepositoryPort locations = mock(LocationRepositoryPort.class);
        service = new CloseAttentionService(professionals, appointments,
                new AppointmentDetailsAssembler(professionals, specialties, locations), CLOCK);
        when(appointments.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(professionals.findByUserId(PRO_USER)).thenReturn(Optional.of(professional(PRO_ID)));
    }

    private Professional professional(Long id) {
        return new Professional(id, PRO_USER, "Pro", "Demo", "pro@example.test", "P-1", "L-1", true,
                List.of(1L), 1L, List.of(1L));
    }

    private Appointment appointment(Long professionalId, AppointmentStatus status, LocalDateTime start) {
        return new Appointment(APPOINTMENT_ID, 30L, professionalId, 1L, 1L, status, null, null, start,
                start.plusMinutes(30), 99L, start.minusDays(1));
    }

    private void given(Appointment a) {
        when(appointments.findByIdForUpdate(APPOINTMENT_ID)).thenReturn(Optional.of(a));
    }

    @Test
    void marksAPastApprovedAppointmentAsCompletedAndAudits() {
        given(appointment(PRO_ID, AppointmentStatus.APPROVED, PAST));

        AppointmentDetails result = service.close(PRO_USER, APPOINTMENT_ID, AttentionOutcome.COMPLETED);

        assertThat(result.appointment().status()).isEqualTo(AppointmentStatus.COMPLETED);
        ArgumentCaptor<StatusHistoryEntry> history = ArgumentCaptor.forClass(StatusHistoryEntry.class);
        verify(appointments).addHistory(history.capture());
        assertThat(history.getValue().status()).isEqualTo(AppointmentStatus.COMPLETED);
        assertThat(history.getValue().source()).isEqualTo(ChangeSource.PROFESSIONAL);
        assertThat(history.getValue().changedByUserId()).isEqualTo(PRO_USER);
    }

    @Test
    void marksAPastApprovedAppointmentAsNoShow() {
        given(appointment(PRO_ID, AppointmentStatus.APPROVED, PAST));

        assertThat(service.close(PRO_USER, APPOINTMENT_ID, AttentionOutcome.NO_SHOW).appointment().status())
                .isEqualTo(AppointmentStatus.NO_SHOW);
    }

    @Test
    void cannotCloseAnotherProfessionalsAppointment() {
        given(appointment(999L, AppointmentStatus.APPROVED, PAST));

        assertThatThrownBy(() -> service.close(PRO_USER, APPOINTMENT_ID, AttentionOutcome.COMPLETED))
                .isInstanceOf(AppointmentAccessDeniedException.class);
        verify(appointments, never()).save(any());
    }

    @Test
    void cannotCloseAnAppointmentThatHasNotHappenedYet() {
        given(appointment(PRO_ID, AppointmentStatus.APPROVED, FUTURE));

        assertThatThrownBy(() -> service.close(PRO_USER, APPOINTMENT_ID, AttentionOutcome.COMPLETED))
                .isInstanceOf(InvalidSchedulingException.class);
        verify(appointments, never()).save(any());
    }

    @Test
    void cannotCloseAnAppointmentThatIsNotApproved() {
        given(appointment(PRO_ID, AppointmentStatus.REQUESTED, PAST));

        assertThatThrownBy(() -> service.close(PRO_USER, APPOINTMENT_ID, AttentionOutcome.COMPLETED))
                .isInstanceOf(InvalidSchedulingException.class);
    }

    @Test
    void aUserThatIsNotAProfessionalCannotClose() {
        when(professionals.findByUserId(PRO_USER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.close(PRO_USER, APPOINTMENT_ID, AttentionOutcome.COMPLETED))
                .isInstanceOf(ProfessionalNotFoundException.class);
    }
}
