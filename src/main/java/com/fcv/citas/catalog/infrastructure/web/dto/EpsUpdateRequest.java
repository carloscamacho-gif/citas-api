package com.fcv.citas.catalog.infrastructure.web.dto;

import com.fcv.citas.catalog.domain.port.in.EpsCommand;
import jakarta.validation.constraints.Size;

/** Cuerpo de actualización de EPS (PATCH). Campos opcionales: se aplica solo lo enviado. */
public record EpsUpdateRequest(@Size(max = 150) String name, Boolean active) {
    public EpsCommand toCommand() {
        return new EpsCommand(null, name, active);
    }
}
