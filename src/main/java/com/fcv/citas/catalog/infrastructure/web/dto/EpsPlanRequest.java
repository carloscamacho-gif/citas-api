package com.fcv.citas.catalog.infrastructure.web.dto;

import com.fcv.citas.catalog.domain.port.in.EpsPlanCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Cuerpo de creación de plan de EPS (POST). Requiere EPS y régimen existentes. */
public record EpsPlanRequest(
        @NotNull Long epsId,
        @NotNull Long regimeId,
        @NotBlank @Size(max = 30) String code,
        @NotBlank @Size(max = 150) String name,
        Boolean active
) {
    public EpsPlanCommand toCommand() {
        return new EpsPlanCommand(epsId, regimeId, code, name, active);
    }
}
