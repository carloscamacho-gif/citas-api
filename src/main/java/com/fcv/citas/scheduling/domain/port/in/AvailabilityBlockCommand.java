package com.fcv.citas.scheduling.domain.port.in;
import java.time.LocalDateTime;
public record AvailabilityBlockCommand(Long locationId, LocalDateTime startAt, LocalDateTime endAt) {}
