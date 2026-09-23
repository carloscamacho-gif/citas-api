package com.fcv.citas.catalog.application;

import com.fcv.citas.catalog.domain.model.InsurancePlan;
import com.fcv.citas.catalog.domain.port.out.InsurancePlanRepositoryPort;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListActiveInsurancePlansServiceTest {
    @Test
    void returnsOnlyTheActivePlansProvidedByTheRepository() {
        InsurancePlanRepositoryPort repository = mock(InsurancePlanRepositoryPort.class);
        List<InsurancePlan> active = List.of(new InsurancePlan(1L, "PLAN-1", "Plan Uno", true));
        when(repository.findActive()).thenReturn(active);

        List<InsurancePlan> result = new ListActiveInsurancePlansService(repository).listActive();

        assertThat(result).containsExactlyElementsOf(active);
        verify(repository).findActive();
    }
}
