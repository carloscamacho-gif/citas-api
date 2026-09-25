package com.fcv.citas.professional.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class ProfessionalLocationValue {
    @Column(name = "location_id") private Long locationId;
    @Column(name = "active") private boolean active;

    protected ProfessionalLocationValue() {}
    public ProfessionalLocationValue(Long locationId) { this.locationId = locationId; this.active = true; }
    public Long getLocationId() { return locationId; }
    @Override public boolean equals(Object o) { return o instanceof ProfessionalLocationValue v && Objects.equals(locationId, v.locationId); }
    @Override public int hashCode() { return Objects.hash(locationId); }
}
