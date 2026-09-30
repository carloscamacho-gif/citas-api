package com.fcv.citas.scheduling.domain.model;

/** Estado de una solicitud de reprogramación. APPROVED/REJECTED son terminales. */
public enum RescheduleStatus {
    PENDING,
    APPROVED,
    REJECTED;

    public boolean isTerminal() {
        return this != PENDING;
    }
}
