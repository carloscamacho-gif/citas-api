package com.fcv.citas.scheduling.domain.model;
import java.time.LocalDateTime;
public record AvailabilityBlock(Long id, Long professionalId, Long locationId, String locationName,
                                LocalDateTime startAt, LocalDateTime endAt, boolean active) {}
