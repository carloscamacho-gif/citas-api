package com.fcv.citas.catalog.application;

import com.fcv.citas.catalog.domain.exception.SpecialtyNotFoundException;
import com.fcv.citas.catalog.domain.model.Specialty;
import com.fcv.citas.catalog.domain.port.in.UpdateSpecialtyCommand;
import com.fcv.citas.catalog.domain.port.out.SpecialtyRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Cubre CA-03 de HU-009 (desactivar en vez de borrar) y la actualización parcial. */
class UpdateSpecialtyServiceTest {

    private SpecialtyRepositoryPort repository;
    private UpdateSpecialtyService service;

    @BeforeEach
    void setUp() {
        repository = mock(SpecialtyRepositoryPort.class);
        service = new UpdateSpecialtyService(repository);
        when(repository.save(any(Specialty.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Specialty existing() {
        return new Specialty(7L, "CAR", "Cardiología", 60, false, true);
    }

    @Test
    void deactivatesInsteadOfDeleting() {
        when(repository.findById(7L)).thenReturn(Optional.of(existing()));

        service.update(7L, new UpdateSpecialtyCommand(null, null, false));

        ArgumentCaptor<Specialty> captor = ArgumentCaptor.forClass(Specialty.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().isActive()).isFalse();
        // Los campos no enviados se conservan.
        assertThat(captor.getValue().getName()).isEqualTo("Cardiología");
        assertThat(captor.getValue().getDurationMinutes()).isEqualTo(60);
    }

    @Test
    void appliesOnlyProvidedFields() {
        when(repository.findById(7L)).thenReturn(Optional.of(existing()));

        service.update(7L, new UpdateSpecialtyCommand("Cardiología clínica", null, null));

        ArgumentCaptor<Specialty> captor = ArgumentCaptor.forClass(Specialty.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Cardiología clínica");
        assertThat(captor.getValue().getDurationMinutes()).isEqualTo(60);
        assertThat(captor.getValue().isActive()).isTrue();
    }

    @Test
    void rejectsUnknownSpecialty() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, new UpdateSpecialtyCommand("x", null, null)))
                .isInstanceOf(SpecialtyNotFoundException.class);

        verify(repository, never()).save(any());
    }
}
