package com.fcv.citas.catalog.domain.port.in;

import com.fcv.citas.catalog.domain.model.InsurancePlan;
import java.util.List;

public interface ListActiveInsurancePlansUseCase {
    List<InsurancePlan> listActive();
}
