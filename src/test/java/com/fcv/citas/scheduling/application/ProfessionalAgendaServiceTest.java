package com.fcv.citas.scheduling.application;

import com.fcv.citas.catalog.domain.port.out.LocationRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.SpecialtyRepositoryPort;
import com.fcv.citas.professional.domain.exception.ProfessionalNotFoundException;
import com.fcv.citas.professional.domain.model.Professional;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.AppointmentDetails;
import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.port.out.AppointmentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** HU-020 / RF-16: agenda de solo lectura, acotada a las citas APPROVED del propio profesional. */
class ProfessionalAgendaServiceTest {

    private static final Long PRO_USER = 7L;
    private static final Long PRO_ID = 1L;
    private static final LocalDate FROM = LocalDate.of(2026, 10, 5);
    private static final LocalDate TO = LocalDate.of(2026, 10, 11);

    private ProfessionalRepositoryPort professionals;
    private AppointmentRepositoryPort appointments;
    private ProfessionalAgendaService service;

    @BeforeEach
    void setUp() {
        professionals = mock(ProfessionalRepositoryPort.class);
        appointments = mock(AppointmentRepositoryPort.class);
        SpecialtyRepositoryPort specialties = mock(SpecialtyRepositoryPort.class);
        LocationRepositoryPort locations = mock(LocationRepositoryPort.class);
        ProfessionalRepositoryPort professionalsForAssembler = professionals;
        service = new ProfessionalAgendaService(professionals, appointments,
                new AppointmentDetailsAssembler(professionalsForAssembler, specialties, locations));
    }

    private Professional professional() {
        return new Professional(PRO_ID, PRO_USER, "Pro", "Demo", "pro@example.test", "P-1", "L-1", true,
                List.of(1L), 1L, List.of(1L));
    }

    private Appointment approved(Long id, LocalDateTime start) {
        return new Appointment(id, 30L, PRO_ID, 1L, 1L, AppointmentStatus.APPROVED, null, null, start,
                start.plusMinutes(30), 99L, start.minusDays(1));
    }

    @Test
    void agendaIsScopedToTheAuthenticatedProfessionalAndApprovedStatus() {
        when(professionals.findByUserId(PRO_USER)).thenReturn(Optional.of(professional()));
        when(appointments.findByProfessional(eq(PRO_ID), eq(AppointmentStatus.APPROVED), eq(FROM), eq(TO), isNull()))
                .thenReturn(List.of(approved(1L, LocalDateTime.of(2026, 10, 6, 8, 0))));

        List<AppointmentDetails> result = service.agenda(PRO_USER, FROM, TO, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).appointment().status()).isEqualTo(AppointmentStatus.APPROVED);
        // Consulta acotada al profesional resuelto desde el token: nunca a otro (CA-01 / RF-16).
        verify(appointments).findByProfessional(PRO_ID, AppointmentStatus.APPROVED, FROM, TO, null);
    }

    @Test
    void locationAndDateRangeFiltersArePassedThrough() {
        when(professionals.findByUserId(PRO_USER)).thenReturn(Optional.of(professional()));
        when(appointments.findByProfessional(PRO_ID, AppointmentStatus.APPROVED, FROM, TO, 2L))
                .thenReturn(List.of());

        service.agenda(PRO_USER, FROM, TO, 2L);

        verify(appointments).findByProfessional(PRO_ID, AppointmentStatus.APPROVED, FROM, TO, 2L);
    }

    @Test
    void aUserThatIsNotAProfessionalHasNoAgenda() {
        when(professionals.findByUserId(PRO_USER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.agenda(PRO_USER, FROM, TO, null))
                .isInstanceOf(ProfessionalNotFoundException.class);
    }
}
