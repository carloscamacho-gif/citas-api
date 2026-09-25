package com.fcv.citas.scheduling.domain.model;
import java.time.LocalDateTime;
public record AvailabilitySlot(Long id, Long blockId, LocalDateTime startAt, LocalDateTime endAt, Long appointmentId) {}
