package com.fcv.citas.scheduling.application;

import com.fcv.citas.catalog.domain.port.out.LocationRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.SpecialtyRepositoryPort;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.AppointmentDetails;
import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.port.out.AppointmentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** S5 (WF-001/WF-003): listado de citas APPROVED en un rango para las automatizaciones. */
class UpcomingAppointmentsServiceTest {

    // "Hoy" fijo: 2026-09-25 (Bogotá).
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-09-25T15:00:00Z"), ZoneId.of("America/Bogota"));

    private AppointmentRepositoryPort appointments;
    private UpcomingAppointmentsService service;

    @BeforeEach
    void setUp() {
        appointments = mock(AppointmentRepositoryPort.class);
        ProfessionalRepositoryPort professionals = mock(ProfessionalRepositoryPort.class);
        SpecialtyRepositoryPort specialties = mock(SpecialtyRepositoryPort.class);
        LocationRepositoryPort locations = mock(LocationRepositoryPort.class);
        service = new UpcomingAppointmentsService(appointments,
                new AppointmentDetailsAssembler(professionals, specialties, locations), CLOCK);
    }

    private Appointment approved() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 25, 16, 0);
        return new Appointment(5L, 30L, 7L, 1L, 1L, AppointmentStatus.APPROVED, null, null, start,
                start.plusMinutes(30), 1L, start.minusDays(1));
    }

    @Test
    void defaultsToTodayThroughTomorrowAndOnlyApproved() {
        when(appointments.findByStatusInRange(eq(AppointmentStatus.APPROVED), eq(LocalDate.of(2026, 9, 25)),
                eq(LocalDate.of(2026, 9, 26)), isNull())).thenReturn(List.of(approved()));

        List<AppointmentDetails> result = service.upcoming(null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).appointment().status()).isEqualTo(AppointmentStatus.APPROVED);
        verify(appointments).findByStatusInRange(AppointmentStatus.APPROVED, LocalDate.of(2026, 9, 25),
                LocalDate.of(2026, 9, 26), null);
    }

    @Test
    void passesExplicitRangeAndLocationThrough() {
        LocalDate from = LocalDate.of(2026, 10, 1);
        LocalDate to = LocalDate.of(2026, 10, 7);
        when(appointments.findByStatusInRange(AppointmentStatus.APPROVED, from, to, 2L)).thenReturn(List.of());

        service.upcoming(from, to, 2L);

        verify(appointments).findByStatusInRange(AppointmentStatus.APPROVED, from, to, 2L);
    }
}
