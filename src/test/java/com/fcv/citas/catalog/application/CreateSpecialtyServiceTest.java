package com.fcv.citas.catalog.application;

import com.fcv.citas.catalog.domain.exception.InvalidSpecialtyDurationException;
import com.fcv.citas.catalog.domain.exception.SpecialtyCodeAlreadyUsedException;
import com.fcv.citas.catalog.domain.model.Specialty;
import com.fcv.citas.catalog.domain.port.in.CreateSpecialtyCommand;
import com.fcv.citas.catalog.domain.port.out.SpecialtyRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Cubre CA-01/CA-02 de HU-009 (CRUD de especialidades) — alta. */
class CreateSpecialtyServiceTest {

    private SpecialtyRepositoryPort repository;
    private CreateSpecialtyService service;

    @BeforeEach
    void setUp() {
        repository = mock(SpecialtyRepositoryPort.class);
        service = new CreateSpecialtyService(repository);
    }

    @Test
    void createsSpecialtyWithValidDuration() {
        var command = new CreateSpecialtyCommand("DER", "Dermatología", 30, false);
        when(repository.existsByCode("DER")).thenReturn(false);
        when(repository.save(any(Specialty.class))).thenAnswer(inv -> inv.getArgument(0));

        Specialty result = service.create(command);

        ArgumentCaptor<Specialty> captor = ArgumentCaptor.forClass(Specialty.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getCode()).isEqualTo("DER");
        assertThat(captor.getValue().getDurationMinutes()).isEqualTo(30);
        assertThat(captor.getValue().isActive()).isTrue();
        assertThat(result.getName()).isEqualTo("Dermatología");
    }

    @Test
    void rejectsDuplicateCode() {
        var command = new CreateSpecialtyCommand("MG", "Medicina General", 30, true);
        when(repository.existsByCode("MG")).thenReturn(true);

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(SpecialtyCodeAlreadyUsedException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void rejectsInvalidDuration() {
        var command = new CreateSpecialtyCommand("XyZ", "Duración inválida", 45, false);
        when(repository.existsByCode("XyZ")).thenReturn(false);

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(InvalidSpecialtyDurationException.class);

        verify(repository, never()).save(any());
    }
}
