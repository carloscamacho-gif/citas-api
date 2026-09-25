package com.fcv.citas.scheduling.infrastructure.web.dto;

import com.fcv.citas.scheduling.domain.model.AvailableProfessional;
import com.fcv.citas.shared.web.ApiTime;

import java.time.OffsetDateTime;
import java.util.List;

/** Coincide con AvailableProfessional del frontend: { id, name, slots[{ startAt, endAt }] }. */
public record AvailableProfessionalResponse(String id, String name, List<SlotResponse> slots) {

    public record SlotResponse(OffsetDateTime startAt, OffsetDateTime endAt) {
    }

    public static AvailableProfessionalResponse from(AvailableProfessional p) {
        return new AvailableProfessionalResponse(String.valueOf(p.id()), p.name(), p.slots().stream()
                .map(w -> new SlotResponse(ApiTime.toOffset(w.startAt()), ApiTime.toOffset(w.endAt())))
                .toList());
    }
}
