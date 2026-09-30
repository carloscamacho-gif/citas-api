package com.fcv.citas.professional.domain.model;

import java.util.List;

public record Professional(Long id, Long userId, String firstName, String lastName, String email,
                           String professionalCode, String licenseNumber, boolean active,
                           List<Long> specialtyIds, Long primarySpecialtyId, List<Long> locationIds) {

    public String fullName() {
        return (firstName + " " + lastName).trim();
    }

    public boolean supports(Long specialtyId, Long locationId) {
        return active && specialtyIds.contains(specialtyId) && locationIds.contains(locationId);
    }

    public Professional withActive(boolean nextActive) {
        return new Professional(id, userId, firstName, lastName, email, professionalCode, licenseNumber,
                nextActive, specialtyIds, primarySpecialtyId, locationIds);
    }

    public Professional withSpecialties(List<Long> nextSpecialties, Long nextPrimary) {
        return new Professional(id, userId, firstName, lastName, email, professionalCode, licenseNumber,
                active, List.copyOf(nextSpecialties), nextPrimary, locationIds);
    }

    public Professional withLocations(List<Long> nextLocations) {
        return new Professional(id, userId, firstName, lastName, email, professionalCode, licenseNumber,
                active, specialtyIds, primarySpecialtyId, List.copyOf(nextLocations));
    }
}
