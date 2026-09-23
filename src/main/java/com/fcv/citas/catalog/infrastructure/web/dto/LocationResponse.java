package com.fcv.citas.catalog.infrastructure.web.dto;

import com.fcv.citas.catalog.domain.model.Location;

/** Coincide con CatalogItem del frontend: { id, name, code, active }. */
public record LocationResponse(Long id, String code, String name, boolean active) {

    public static LocationResponse from(Location l) {
        return new LocationResponse(l.id(), l.code(), l.name(), l.active());
    }
}
