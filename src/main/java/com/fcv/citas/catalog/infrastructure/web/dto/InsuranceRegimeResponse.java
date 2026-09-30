package com.fcv.citas.catalog.infrastructure.web.dto;

import com.fcv.citas.catalog.domain.model.InsuranceRegime;

public record InsuranceRegimeResponse(String id, String code, String name) {
    public static InsuranceRegimeResponse from(InsuranceRegime regime) {
        return new InsuranceRegimeResponse(String.valueOf(regime.id()), regime.code(), regime.name());
    }
}
