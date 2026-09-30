package com.fcv.citas.scheduling.infrastructure.web.dto;

import com.fcv.citas.scheduling.domain.model.AvailabilityBlock;
import com.fcv.citas.shared.web.ApiTime;

import java.time.OffsetDateTime;

/** Coincide con AvailabilityBlock del frontend: ids como string, fechas ISO con offset. */
public record AvailabilityBlockResponse(String id, String locationId, String locationName,
                                        OffsetDateTime startAt, OffsetDateTime endAt) {

    public static AvailabilityBlockResponse from(AvailabilityBlock b) {
        return new AvailabilityBlockResponse(String.valueOf(b.id()), String.valueOf(b.locationId()),
                b.locationName(), ApiTime.toOffset(b.startAt()), ApiTime.toOffset(b.endAt()));
    }
}
