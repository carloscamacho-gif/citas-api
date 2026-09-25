package com.fcv.citas.professional.application;

import com.fcv.citas.auth.domain.exception.DocumentAlreadyUsedException;
import com.fcv.citas.auth.domain.exception.EmailAlreadyUsedException;
import com.fcv.citas.auth.domain.model.DocumentType;
import com.fcv.citas.auth.domain.model.RoleName;
import com.fcv.citas.auth.domain.model.User;
import com.fcv.citas.auth.domain.port.out.PasswordHasherPort;
import com.fcv.citas.auth.domain.port.out.UserRepositoryPort;
import com.fcv.citas.catalog.domain.model.Location;
import com.fcv.citas.catalog.domain.model.Specialty;
import com.fcv.citas.catalog.domain.port.out.LocationRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.SpecialtyRepositoryPort;
import com.fcv.citas.professional.domain.exception.InvalidProfessionalException;
import com.fcv.citas.professional.domain.exception.ProfessionalNotFoundException;
import com.fcv.citas.professional.domain.model.Professional;
import com.fcv.citas.professional.domain.port.in.CreateProfessionalCommand;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** RF-07 / HU-010 y HU-011: alta y configuración de profesionales por ADMIN. */
class ProfessionalManagementServiceTest {

    private ProfessionalRepositoryPort professionals;
    private UserRepositoryPort users;
    private PasswordHasherPort hasher;
    private SpecialtyRepositoryPort specialties;
    private LocationRepositoryPort locations;
    private ProfessionalManagementService service;

    @BeforeEach
    void setUp() {
        professionals = mock(ProfessionalRepositoryPort.class);
        users = mock(UserRepositoryPort.class);
        hasher = mock(PasswordHasherPort.class);
        specialties = mock(SpecialtyRepositoryPort.class);
        locations = mock(LocationRepositoryPort.class);
        service = new ProfessionalManagementService(professionals, users, hasher, specialties, locations);

        when(specialties.findById(1L)).thenReturn(Optional.of(new Specialty(1L, "MG", "Medicina General", 30, true, true)));
        when(specialties.findById(2L)).thenReturn(Optional.of(new Specialty(2L, "CAR", "Cardiología", 60, false, true)));
        when(specialties.findById(3L)).thenReturn(Optional.of(new Specialty(3L, "OLD", "Retirada", 30, false, false)));
        when(locations.findById(1L)).thenReturn(Optional.of(new Location(1L, "HIC", "Hospital", true)));
        when(locations.findById(2L)).thenReturn(Optional.of(new Location(2L, "ICV", "Instituto", true)));
        when(hasher.hash("Demo1234*")).thenReturn("hashed");
        when(users.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.assignId(70L);
            return u;
        });
        when(professionals.save(any(Professional.class))).thenAnswer(inv -> {
            Professional p = inv.getArgument(0);
            return new Professional(7L, p.userId(), p.firstName(), p.lastName(), p.email(), p.professionalCode(),
                    p.licenseNumber(), p.active(), p.specialtyIds(), p.primarySpecialtyId(), p.locationIds());
        });
    }

    private CreateProfessionalCommand command(List<Long> specialtyIds, Long primary, List<Long> locationIds) {
        return new CreateProfessionalCommand("Laura", "Demo", DocumentType.CC, "990001", "laura@example.test",
                "3000000001", "Demo1234*", "PRO-001", "LIC-001", specialtyIds, primary, locationIds);
    }

    @Test
    void createsAProfessionalUserWithHashedPasswordAndAssignments() {
        Professional created = service.create(command(List.of(1L, 2L), 1L, List.of(1L, 2L)));

        ArgumentCaptor<User> user = ArgumentCaptor.forClass(User.class);
        verify(users).save(user.capture());
        assertThat(user.getValue().getRoles()).containsExactly(RoleName.PROFESSIONAL);
        assertThat(user.getValue().getPasswordHash()).isEqualTo("hashed");
        assertThat(created.active()).isTrue();
        assertThat(created.userId()).isEqualTo(70L);
        assertThat(created.primarySpecialtyId()).isEqualTo(1L);
        assertThat(created.locationIds()).containsExactly(1L, 2L);
    }

    @Test
    void requiresExactlyOnePrimarySpecialtyIncludedInTheList() {
        assertThatThrownBy(() -> service.create(command(List.of(1L, 2L), 9L, List.of(1L))))
                .isInstanceOf(InvalidProfessionalException.class).hasMessageContaining("primaria");
        assertThatThrownBy(() -> service.create(command(List.of(1L, 2L), null, List.of(1L))))
                .isInstanceOf(InvalidProfessionalException.class);
        verify(users, never()).save(any());
    }

    @Test
    void rejectsInactiveOrUnknownSpecialties() {
        assertThatThrownBy(() -> service.create(command(List.of(1L, 3L), 1L, List.of(1L))))
                .isInstanceOf(InvalidProfessionalException.class).hasMessageContaining("inactiva");
        assertThatThrownBy(() -> service.create(command(List.of(1L, 99L), 1L, List.of(1L))))
                .isInstanceOf(InvalidProfessionalException.class).hasMessageContaining("no existe");
    }

    @Test
    void rejectsMissingUnknownOrDuplicatedLocations() {
        assertThatThrownBy(() -> service.create(command(List.of(1L), 1L, List.of())))
                .isInstanceOf(InvalidProfessionalException.class);
        assertThatThrownBy(() -> service.create(command(List.of(1L), 1L, List.of(1L, 1L))))
                .isInstanceOf(InvalidProfessionalException.class).hasMessageContaining("duplicadas");
        assertThatThrownBy(() -> service.create(command(List.of(1L), 1L, List.of(99L))))
                .isInstanceOf(InvalidProfessionalException.class);
    }

    @Test
    void rejectsDuplicateIdentityData() {
        when(users.existsByEmail("laura@example.test")).thenReturn(true);
        assertThatThrownBy(() -> service.create(command(List.of(1L), 1L, List.of(1L))))
                .isInstanceOf(EmailAlreadyUsedException.class);

        when(users.existsByEmail("laura@example.test")).thenReturn(false);
        when(users.existsByDocument(DocumentType.CC, "990001")).thenReturn(true);
        assertThatThrownBy(() -> service.create(command(List.of(1L), 1L, List.of(1L))))
                .isInstanceOf(DocumentAlreadyUsedException.class);

        when(users.existsByDocument(DocumentType.CC, "990001")).thenReturn(false);
        when(professionals.existsByProfessionalCode("PRO-001")).thenReturn(true);
        assertThatThrownBy(() -> service.create(command(List.of(1L), 1L, List.of(1L))))
                .isInstanceOf(InvalidProfessionalException.class).hasMessageContaining("código");

        when(professionals.existsByProfessionalCode("PRO-001")).thenReturn(false);
        when(professionals.existsByLicenseNumber("LIC-001")).thenReturn(true);
        assertThatThrownBy(() -> service.create(command(List.of(1L), 1L, List.of(1L))))
                .isInstanceOf(InvalidProfessionalException.class).hasMessageContaining("matrícula");
        verify(users, never()).save(any());
    }

    @Test
    void deactivatingKeepsTheRestOfTheProfessional() {
        var existing = new Professional(7L, 70L, "Laura", "Demo", "l@x.test", "P1", "L1", true, List.of(1L), 1L, List.of(1L));
        when(professionals.findById(7L)).thenReturn(Optional.of(existing));

        Professional result = service.setActive(7L, false);

        assertThat(result.active()).isFalse();
        assertThat(result.specialtyIds()).containsExactly(1L);
    }

    @Test
    void reassigningLocationsValidatesThem() {
        var existing = new Professional(7L, 70L, "Laura", "Demo", "l@x.test", "P1", "L1", true, List.of(1L), 1L, List.of(1L));
        when(professionals.findById(7L)).thenReturn(Optional.of(existing));

        assertThat(service.assignLocations(7L, List.of(1L, 2L)).locationIds()).containsExactly(1L, 2L);
        assertThatThrownBy(() -> service.assignLocations(7L, List.of()))
                .isInstanceOf(InvalidProfessionalException.class);
    }

    @Test
    void unknownProfessionalIsNotFound() {
        when(professionals.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.setActive(404L, true)).isInstanceOf(ProfessionalNotFoundException.class);
    }
}
