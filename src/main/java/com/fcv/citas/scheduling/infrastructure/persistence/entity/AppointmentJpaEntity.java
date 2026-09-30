package com.fcv.citas.scheduling.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class AppointmentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_user_id", nullable = false)
    private Long patientUserId;

    @Column(name = "professional_id", nullable = false)
    private Long professionalId;

    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @Column(name = "specialty_id", nullable = false)
    private Long specialtyId;

    @Column(name = "status_id", nullable = false)
    private Long statusId;

    private String reason;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "scheduled_start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "scheduled_end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "approved_by_user_id")
    private Long approvedByUserId;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    protected AppointmentJpaEntity() {
        // JPA
    }

    public AppointmentJpaEntity(Long id, Long patientUserId, Long professionalId, Long locationId, Long specialtyId,
                                Long statusId, String reason, String rejectionReason, LocalDateTime startAt,
                                LocalDateTime endAt, Long approvedByUserId, LocalDateTime approvedAt) {
        this.id = id;
        this.patientUserId = patientUserId;
        this.professionalId = professionalId;
        this.locationId = locationId;
        this.specialtyId = specialtyId;
        this.statusId = statusId;
        this.reason = reason;
        this.rejectionReason = rejectionReason;
        this.startAt = startAt;
        this.endAt = endAt;
        this.approvedByUserId = approvedByUserId;
        this.approvedAt = approvedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getPatientUserId() {
        return patientUserId;
    }

    public Long getProfessionalId() {
        return professionalId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public Long getSpecialtyId() {
        return specialtyId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public String getReason() {
        return reason;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public Long getApprovedByUserId() {
        return approvedByUserId;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
}
