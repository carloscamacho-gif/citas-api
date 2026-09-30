package com.fcv.citas.scheduling.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Catálogo fijo de estados de cita (tabla appointment_statuses, sembrada en V8). */
@Entity
@Table(name = "appointment_statuses")
public class AppointmentStatusJpaEntity {

    @Id
    private Long id;

    private String code;

    protected AppointmentStatusJpaEntity() {
        // JPA
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }
}
