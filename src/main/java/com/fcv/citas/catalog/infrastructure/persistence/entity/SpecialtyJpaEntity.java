package com.fcv.citas.catalog.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "specialties")
public class SpecialtyJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String name;

    @Column(name = "duration_minutes")
    private int durationMinutes;

    @Column(name = "is_general")
    private boolean general;

    private boolean active;

    protected SpecialtyJpaEntity() {
        // JPA
    }

    public SpecialtyJpaEntity(Long id, String code, String name, int durationMinutes, boolean general, boolean active) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.general = general;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public boolean isGeneral() {
        return general;
    }

    public boolean isActive() {
        return active;
    }
}
