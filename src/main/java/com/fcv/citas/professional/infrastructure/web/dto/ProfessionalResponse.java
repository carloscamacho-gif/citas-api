package com.fcv.citas.professional.infrastructure.web.dto;

import com.fcv.citas.professional.domain.model.Professional;
import java.util.List;

public record ProfessionalResponse(Long id, String firstName, String lastName, String email,
        String professionalCode, String licenseNumber, boolean active, List<Long> specialtyIds,
        Long primarySpecialtyId, List<Long> locationIds) {
    public static ProfessionalResponse from(Professional p){return new ProfessionalResponse(p.id(),p.firstName(),p.lastName(),p.email(),p.professionalCode(),p.licenseNumber(),p.active(),p.specialtyIds(),p.primarySpecialtyId(),p.locationIds());}
}
