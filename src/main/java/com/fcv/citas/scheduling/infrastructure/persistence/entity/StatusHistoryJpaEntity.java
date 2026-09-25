package com.fcv.citas.scheduling.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** Fila append-only de auditoría (RN-12): la aplicación solo inserta y lee, nunca actualiza ni borra. */
@Entity
@Table(name = "appointment_status_history")
public class StatusHistoryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_id", nullable = false, updatable = false)
    private Long appointmentId;

    @Column(name = "status_id", nullable = false, updatable = false)
    private Long statusId;

    @Column(name = "changed_by_user_id", updatable = false)
    private Long changedByUserId;

    @Column(name = "change_source", nullable = false, updatable = false)
    private String changeSource;

    @Column(updatable = false)
    private String reason;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    protected StatusHistoryJpaEntity() {
        // JPA
    }

    public StatusHistoryJpaEntity(Long appointmentId, Long statusId, Long changedByUserId, String changeSource,
                                  String reason, LocalDateTime changedAt) {
        this.appointmentId = appointmentId;
        this.statusId = statusId;
        this.changedByUserId = changedByUserId;
        this.changeSource = changeSource;
        this.reason = reason;
        this.changedAt = changedAt;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public Long getChangedByUserId() {
        return changedByUserId;
    }

    public String getChangeSource() {
        return changeSource;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }
}
