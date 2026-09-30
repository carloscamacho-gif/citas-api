package com.fcv.citas.scheduling.application;

import com.fcv.citas.professional.domain.model.Professional;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import com.fcv.citas.scheduling.domain.exception.AppointmentAccessDeniedException;
import com.fcv.citas.scheduling.domain.exception.AppointmentNotFoundException;
import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.model.ChangeSource;
import com.fcv.citas.scheduling.domain.model.StatusHistoryEntry;
import com.fcv.citas.scheduling.domain.port.out.AppointmentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** HU-024 / sección 8 del PRD: USER ve sus citas, PROFESSIONAL las suyas, ADMIN todas. */
class AppointmentHistoryServiceTest {

    private static final Long APPOINTMENT_ID = 500L;
    private static final Long PATIENT = 30L;
    private static final LocalDateTime START = LocalDateTime.of(2026, 10, 6, 8, 0);

    private AppointmentRepositoryPort appointments;
    private ProfessionalRepositoryPort professionals;
    private AppointmentHistoryService service;
    private final List<StatusHistoryEntry> entries = List.of(new StatusHistoryEntry(APPOINTMENT_ID,
            AppointmentStatus.APPROVED, PATIENT, ChangeSource.SYSTEM, null, START.minusDays(1)));

    @BeforeEach
    void setUp() {
        appointments = mock(AppointmentRepositoryPort.class);
        professionals = mock(ProfessionalRepositoryPort.class);
        service = new AppointmentHistoryService(appointments, professionals);
        when(appointments.findById(APPOINTMENT_ID)).thenReturn(Optional.of(new Appointment(APPOINTMENT_ID, PATIENT, 7L,
                1L, 1L, AppointmentStatus.APPROVED, null, null, START, START.plusMinutes(30), null, null)));
        when(appointments.history(APPOINTMENT_ID)).thenReturn(entries);
        when(professionals.findByUserId(70L)).thenReturn(Optional.of(professional(7L, 70L)));
        when(professionals.findByUserId(80L)).thenReturn(Optional.of(professional(8L, 80L)));
    }

    private static Professional professional(long id, long userId) {
        return new Professional(id, userId, "P", "Q", "p@x.test", "C" + id, "L" + id, true, List.of(1L), 1L, List.of(1L));
    }

    @Test
    void thePatientSeesTheirOwnHistory() {
        assertThat(service.history(PATIENT, Set.of("USER"), APPOINTMENT_ID)).isEqualTo(entries);
    }

    @Test
    void anotherUserIsDenied() {
        assertThatThrownBy(() -> service.history(31L, Set.of("USER"), APPOINTMENT_ID))
                .isInstanceOf(AppointmentAccessDeniedException.class);
    }

    @Test
    void theAssignedProfessionalSeesIt() {
        assertThat(service.history(70L, Set.of("PROFESSIONAL"), APPOINTMENT_ID)).isEqualTo(entries);
    }

    @Test
    void anotherProfessionalIsDenied() {
        assertThatThrownBy(() -> service.history(80L, Set.of("PROFESSIONAL"), APPOINTMENT_ID))
                .isInstanceOf(AppointmentAccessDeniedException.class);
    }

    @Test
    void adminSeesEverything() {
        assertThat(service.history(1L, Set.of("ADMIN"), APPOINTMENT_ID)).isEqualTo(entries);
    }

    @Test
    void unknownAppointmentIsNotFound() {
        when(appointments.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.history(1L, Set.of("ADMIN"), 999L)).isInstanceOf(AppointmentNotFoundException.class);
    }
}
