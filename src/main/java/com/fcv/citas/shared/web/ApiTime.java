package com.fcv.citas.shared.web;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * Frontera de zona horaria de la API: el contrato REST intercambia ISO-8601 con offset, mientras que el
 * dominio trabaja con hora local de la zona operativa del laboratorio (America/Bogota).
 */
public final class ApiTime {

    public static final ZoneId ZONE = ZoneId.of("America/Bogota");

    private ApiTime() {
    }

    public static LocalDateTime toLocal(OffsetDateTime value) {
        return value == null ? null : value.atZoneSameInstant(ZONE).toLocalDateTime();
    }

    public static OffsetDateTime toOffset(LocalDateTime value) {
        return value == null ? null : value.atZone(ZONE).toOffsetDateTime();
    }
}
