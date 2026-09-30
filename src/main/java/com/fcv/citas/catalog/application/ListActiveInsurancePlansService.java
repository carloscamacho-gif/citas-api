package com.fcv.citas.catalog.application;

import com.fcv.citas.catalog.domain.model.InsurancePlan;
import com.fcv.citas.catalog.domain.port.in.ListActiveInsurancePlansUseCase;
import com.fcv.citas.catalog.domain.port.out.InsurancePlanRepositoryPort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ListActiveInsurancePlansService implements ListActiveInsurancePlansUseCase {
    private final InsurancePlanRepositoryPort plans;
    public ListActiveInsurancePlansService(InsurancePlanRepositoryPort plans) { this.plans = plans; }
    @Override public List<InsurancePlan> listActive() { return plans.findActive(); }
}
