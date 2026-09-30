package com.fcv.citas.catalog.infrastructure.web.dto;

import com.fcv.citas.catalog.domain.model.Eps;

public record EpsResponse(String id, String code, String name, boolean active) {
    public static EpsResponse from(Eps eps) {
        return new EpsResponse(String.valueOf(eps.id()), eps.code(), eps.name(), eps.active());
    }
}
