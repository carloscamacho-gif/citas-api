package com.fcv.citas.professional.infrastructure.persistence.entity;

import com.fcv.citas.auth.infrastructure.persistence.entity.UserJpaEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "professionals")
public class ProfessionalJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch = FetchType.EAGER) @JoinColumn(name = "user_id", nullable = false) private UserJpaEntity user;
    @Column(name = "professional_code", nullable = false) private String professionalCode;
    @Column(name = "license_number", nullable = false) private String licenseNumber;
    @Column(nullable = false) private boolean active;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "professional_specialties", joinColumns = @JoinColumn(name = "professional_id"))
    private Set<ProfessionalSpecialtyValue> specialties = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "professional_locations", joinColumns = @JoinColumn(name = "professional_id"))
    private Set<ProfessionalLocationValue> locations = new HashSet<>();

    protected ProfessionalJpaEntity() {}
    public ProfessionalJpaEntity(Long id, UserJpaEntity user, String professionalCode, String licenseNumber,
            boolean active, Set<ProfessionalSpecialtyValue> specialties, Set<ProfessionalLocationValue> locations) {
        this.id=id; this.user=user; this.professionalCode=professionalCode; this.licenseNumber=licenseNumber;
        this.active=active; this.specialties=specialties; this.locations=locations;
    }
    public Long getId(){return id;} public UserJpaEntity getUser(){return user;}
    public String getProfessionalCode(){return professionalCode;} public String getLicenseNumber(){return licenseNumber;}
    public boolean isActive(){return active;} public Set<ProfessionalSpecialtyValue> getSpecialties(){return specialties;}
    public Set<ProfessionalLocationValue> getLocations(){return locations;}
}
