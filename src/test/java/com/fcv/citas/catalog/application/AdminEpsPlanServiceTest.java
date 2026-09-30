package com.fcv.citas.catalog.application;

import com.fcv.citas.catalog.domain.exception.CatalogCodeAlreadyUsedException;
import com.fcv.citas.catalog.domain.exception.CatalogInUseException;
import com.fcv.citas.catalog.domain.exception.EpsNotFoundException;
import com.fcv.citas.catalog.domain.model.EpsPlan;
import com.fcv.citas.catalog.domain.port.in.EpsPlanCommand;
import com.fcv.citas.catalog.domain.port.out.EpsPlanRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.EpsRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.InsuranceRegimeRepositoryPort;
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

/** HU-008 / RF-06: CRUD de planes; requiere EPS y régimen válidos; desactivación en vez de borrado si hay afiliaciones. */
class AdminEpsPlanServiceTest {

    private EpsPlanRepositoryPort plans;
    private EpsRepositoryPort eps;
    private InsuranceRegimeRepositoryPort regimes;
    private AdminEpsPlanService service;

    @BeforeEach
    void setUp() {
        plans = mock(EpsPlanRepositoryPort.class);
        eps = mock(EpsRepositoryPort.class);
        regimes = mock(InsuranceRegimeRepositoryPort.class);
        service = new AdminEpsPlanService(plans, eps, regimes);
        when(plans.save(any())).thenAnswer(inv -> {
            EpsPlan p = inv.getArgument(0);
            return new EpsPlan(p.id() == null ? 77L : p.id(), p.epsId(), "EPS Uno", p.regimeId(), "Contributivo",
                    p.code(), p.name(), p.active());
        });
    }

    @Test
    void createsAPlanForAnExistingEpsAndRegime() {
        when(eps.exists(1L)).thenReturn(true);
        when(regimes.exists(2L)).thenReturn(true);
        when(plans.existsByCode("PLAN-2")).thenReturn(false);

        EpsPlan created = service.create(new EpsPlanCommand(1L, 2L, "PLAN-2", "Plan Dos", null));

        assertThat(created.id()).isEqualTo(77L);
        assertThat(created.epsId()).isEqualTo(1L);
        assertThat(created.regimeId()).isEqualTo(2L);
        assertThat(created.active()).isTrue();
    }

    @Test
    void rejectsAPlanForANonExistentEps() {
        when(eps.exists(999L)).thenReturn(false);

        assertThatThrownBy(() -> service.create(new EpsPlanCommand(999L, 2L, "PLAN-X", "X", null)))
                .isInstanceOf(EpsNotFoundException.class);
        verify(plans, never()).save(any());
    }

    @Test
    void rejectsAPlanForANonExistentRegime() {
        when(eps.exists(1L)).thenReturn(true);
        when(regimes.exists(999L)).thenReturn(false);

        assertThatThrownBy(() -> service.create(new EpsPlanCommand(1L, 999L, "PLAN-X", "X", null)))
                .isInstanceOf(IllegalArgumentException.class);
        verify(plans, never()).save(any());
    }

    @Test
    void rejectsADuplicatePlanCode() {
        when(eps.exists(1L)).thenReturn(true);
        when(regimes.exists(2L)).thenReturn(true);
        when(plans.existsByCode("PLAN-1")).thenReturn(true);

        assertThatThrownBy(() -> service.create(new EpsPlanCommand(1L, 2L, "PLAN-1", "Dup", null)))
                .isInstanceOf(CatalogCodeAlreadyUsedException.class);
    }

    @Test
    void updateKeepsEpsRegimeAndCodeImmutable() {
        when(plans.findById(5L)).thenReturn(Optional.of(
                new EpsPlan(5L, 1L, "EPS Uno", 2L, "Contributivo", "PLAN-1", "Viejo", true)));

        service.update(5L, new EpsPlanCommand(null, null, null, "Nuevo", false));

        ArgumentCaptor<EpsPlan> saved = ArgumentCaptor.forClass(EpsPlan.class);
        verify(plans).save(saved.capture());
        assertThat(saved.getValue().epsId()).isEqualTo(1L);
        assertThat(saved.getValue().regimeId()).isEqualTo(2L);
        assertThat(saved.getValue().code()).isEqualTo("PLAN-1");
        assertThat(saved.getValue().name()).isEqualTo("Nuevo");
        assertThat(saved.getValue().active()).isFalse();
    }

    @Test
    void cannotDeleteAPlanWithAffiliations() {
        when(plans.findById(5L)).thenReturn(Optional.of(
                new EpsPlan(5L, 1L, "EPS Uno", 2L, "Contributivo", "PLAN-1", "Plan", true)));
        when(plans.isReferenced(5L)).thenReturn(true);

        assertThatThrownBy(() -> service.delete(5L)).isInstanceOf(CatalogInUseException.class);
        verify(plans, never()).delete(any());
    }

    @Test
    void deletesAnUnreferencedPlan() {
        when(plans.findById(5L)).thenReturn(Optional.of(
                new EpsPlan(5L, 1L, "EPS Uno", 2L, "Contributivo", "PLAN-1", "Plan", true)));
        when(plans.isReferenced(5L)).thenReturn(false);

        service.delete(5L);

        verify(plans).delete(5L);
    }
}
