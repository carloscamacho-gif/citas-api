package com.fcv.citas.catalog.application;

import com.fcv.citas.catalog.domain.exception.CatalogCodeAlreadyUsedException;
import com.fcv.citas.catalog.domain.exception.CatalogInUseException;
import com.fcv.citas.catalog.domain.exception.EpsNotFoundException;
import com.fcv.citas.catalog.domain.model.Eps;
import com.fcv.citas.catalog.domain.port.in.EpsCommand;
import com.fcv.citas.catalog.domain.port.out.EpsRepositoryPort;
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

/** HU-007 / RF-06: CRUD de EPS con desactivación en vez de borrado para catálogos referenciados. */
class AdminEpsServiceTest {

    private EpsRepositoryPort repository;
    private AdminEpsService service;

    @BeforeEach
    void setUp() {
        repository = mock(EpsRepositoryPort.class);
        service = new AdminEpsService(repository);
        when(repository.save(any())).thenAnswer(inv -> {
            Eps e = inv.getArgument(0);
            return e.id() == null ? new Eps(99L, e.code(), e.name(), e.active()) : e;
        });
    }

    @Test
    void createsAnEpsWhenCodeIsFree() {
        when(repository.existsByCode("EPS-2")).thenReturn(false);

        Eps created = service.create(new EpsCommand("EPS-2", "EPS Dos", null));

        assertThat(created.id()).isEqualTo(99L);
        assertThat(created.active()).isTrue();
        ArgumentCaptor<Eps> saved = ArgumentCaptor.forClass(Eps.class);
        verify(repository).save(saved.capture());
        assertThat(saved.getValue().code()).isEqualTo("EPS-2");
    }

    @Test
    void rejectsADuplicateCode() {
        when(repository.existsByCode("EPS-1")).thenReturn(true);

        assertThatThrownBy(() -> service.create(new EpsCommand("EPS-1", "EPS Uno", null)))
                .isInstanceOf(CatalogCodeAlreadyUsedException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void updateKeepsTheCodeAndAppliesNameAndActive() {
        when(repository.findById(5L)).thenReturn(Optional.of(new Eps(5L, "EPS-1", "Viejo", true)));

        Eps updated = service.update(5L, new EpsCommand("IGNORED", "Nuevo nombre", false));

        assertThat(updated.code()).isEqualTo("EPS-1");
        assertThat(updated.name()).isEqualTo("Nuevo nombre");
        assertThat(updated.active()).isFalse();
    }

    @Test
    void cannotDeleteAnEpsReferencedByPlans() {
        when(repository.exists(5L)).thenReturn(true);
        when(repository.isReferenced(5L)).thenReturn(true);

        assertThatThrownBy(() -> service.delete(5L)).isInstanceOf(CatalogInUseException.class);
        verify(repository, never()).delete(any());
    }

    @Test
    void deletesAnUnreferencedEps() {
        when(repository.exists(5L)).thenReturn(true);
        when(repository.isReferenced(5L)).thenReturn(false);

        service.delete(5L);

        verify(repository).delete(5L);
    }

    @Test
    void deletingAnUnknownEpsIsNotFound() {
        when(repository.exists(404L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(404L)).isInstanceOf(EpsNotFoundException.class);
    }
}
