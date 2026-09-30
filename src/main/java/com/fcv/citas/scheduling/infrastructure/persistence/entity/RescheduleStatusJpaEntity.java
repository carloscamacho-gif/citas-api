package com.fcv.citas.scheduling.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Catálogo fijo de estados de reprogramación (tabla reschedule_request_statuses, sembrada en V9). */
@Entity
@Table(name = "reschedule_request_statuses")
public class RescheduleStatusJpaEntity {

    @Id
    private Long id;

    private String code;

    protected RescheduleStatusJpaEntity() {
        // JPA
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }
}
