package com.fcv.citas.scheduling.infrastructure.web.dto;

import com.fcv.citas.scheduling.domain.port.in.AvailabilityBlockCommand;
import com.fcv.citas.shared.web.ApiTime;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

/** Cuerpo de POST/PATCH /api/v1/professional/availability-blocks (RF-08). */
public record AvailabilityBlockRequest(
        @NotNull Long locationId,
        @NotNull OffsetDateTime startAt,
        @NotNull OffsetDateTime endAt
) {

    public AvailabilityBlockCommand toCommand() {
        return new AvailabilityBlockCommand(locationId, ApiTime.toLocal(startAt), ApiTime.toLocal(endAt));
    }
}
