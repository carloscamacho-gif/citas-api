package com.fcv.citas.scheduling.domain.model;

import java.time.LocalDateTime;

/** Registro append-only de auditoría de un cambio de estado (RF-19, RN-12). */
public record StatusHistoryEntry(Long appointmentId, AppointmentStatus status, Long changedByUserId,
                                 ChangeSource source, String reason, LocalDateTime changedAt) {
}
