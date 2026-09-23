package com.fcv.citas.catalog.infrastructure.web.dto;

import com.fcv.citas.catalog.domain.model.InsurancePlan;

public record InsurancePlanResponse(Long id, String code, String name, boolean active) {
    public static InsurancePlanResponse from(InsurancePlan plan) {
        return new InsurancePlanResponse(plan.id(), plan.code(), plan.name(), plan.active());
    }
}
