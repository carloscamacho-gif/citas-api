package com.fcv.citas.catalog.infrastructure.web.dto;

import com.fcv.citas.catalog.domain.model.Specialty;

/** Coincide con Specialty del frontend: { id, name, code, active, durationMinutes, appointmentType }. */
public record SpecialtyResponse(Long id, String code, String name, int durationMinutes, boolean active,
                                String appointmentType) {

    public static SpecialtyResponse from(Specialty s) {
        return new SpecialtyResponse(s.getId(), s.getCode(), s.getName(), s.getDurationMinutes(), s.isActive(),
                s.isGeneral() ? "GENERAL" : "SPECIALIZED");
    }
}
