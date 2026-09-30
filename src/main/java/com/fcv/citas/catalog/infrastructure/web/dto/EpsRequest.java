package com.fcv.citas.catalog.infrastructure.web.dto;

import com.fcv.citas.catalog.domain.port.in.EpsCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Cuerpo de creación de EPS (POST). El código es obligatorio e inmutable después. */
public record EpsRequest(
        @NotBlank @Size(max = 30) String code,
        @NotBlank @Size(max = 150) String name,
        Boolean active
) {
    public EpsCommand toCommand() {
        return new EpsCommand(code, name, active);
    }
}
