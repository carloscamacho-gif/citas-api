package com.fcv.citas.scheduling.domain.port.in;

import java.time.LocalDateTime;

/**
 * Solicitud de reprogramación de una cita propia. Se conserva profesional y especialidad; solo cambian
 * fecha/hora (y opcionalmente la sede donde el mismo profesional atiende). {@code professionalId} viaja
 * para rechazar explícitamente cualquier intento de cambiar de profesional (HU-019 CA-03).
 */
public record RescheduleCommand(Long appointmentId, Long professionalId, Long locationId, LocalDateTime requestedStartAt) {
}
