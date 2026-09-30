package com.fcv.citas.catalog.infrastructure.web.dto;

import com.fcv.citas.catalog.domain.port.in.EpsPlanCommand;
import jakarta.validation.constraints.Size;

/** Cuerpo de actualización de plan (PATCH). EPS/régimen/código son inmutables; solo nombre y estado. */
public record EpsPlanUpdateRequest(@Size(max = 150) String name, Boolean active) {
    public EpsPlanCommand toCommand() {
        return new EpsPlanCommand(null, null, null, name, active);
    }
}
