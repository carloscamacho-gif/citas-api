package com.fcv.citas.scheduling.domain.model;

/** Estados de cita (RF-05, catálogo fijo). Los terminales no admiten más transiciones. */
public enum AppointmentStatus {
    REQUESTED,
    APPROVED,
    REJECTED,
    CANCELLED,
    COMPLETED,
    NO_SHOW
}
