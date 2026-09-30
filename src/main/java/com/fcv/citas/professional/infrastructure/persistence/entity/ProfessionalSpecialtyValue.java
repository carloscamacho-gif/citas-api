package com.fcv.citas.professional.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class ProfessionalSpecialtyValue {
    @Column(name = "specialty_id") private Long specialtyId;
    @Column(name = "is_primary") private boolean primary;
    @Column(name = "active") private boolean active;

    protected ProfessionalSpecialtyValue() {}
    public ProfessionalSpecialtyValue(Long specialtyId, boolean primary) {
        this.specialtyId = specialtyId; this.primary = primary; this.active = true;
    }
    public Long getSpecialtyId() { return specialtyId; }
    public boolean isPrimary() { return primary; }
    @Override public boolean equals(Object o) { return o instanceof ProfessionalSpecialtyValue v && Objects.equals(specialtyId, v.specialtyId); }
    @Override public int hashCode() { return Objects.hash(specialtyId); }
}
