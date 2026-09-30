package com.fcv.citas.catalog.domain.port.out;

import com.fcv.citas.catalog.domain.model.InsurancePlan;
import java.util.List;

public interface InsurancePlanRepositoryPort {
    List<InsurancePlan> findActive();
    boolean isActive(Long id);
}
