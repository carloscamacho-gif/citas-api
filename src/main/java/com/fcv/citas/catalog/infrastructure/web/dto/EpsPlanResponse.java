package com.fcv.citas.catalog.infrastructure.web.dto;

import com.fcv.citas.catalog.domain.model.EpsPlan;

public record EpsPlanResponse(String id, String epsId, String epsName, String regimeId, String regimeName,
                              String code, String name, boolean active) {
    public static EpsPlanResponse from(EpsPlan plan) {
        return new EpsPlanResponse(String.valueOf(plan.id()), String.valueOf(plan.epsId()), plan.epsName(),
                String.valueOf(plan.regimeId()), plan.regimeName(), plan.code(), plan.name(), plan.active());
    }
}
