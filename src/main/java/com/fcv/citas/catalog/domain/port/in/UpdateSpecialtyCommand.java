package com.fcv.citas.catalog.domain.port.in;

/** Actualización parcial (PATCH): un campo en null significa "no cambiar". */
public record UpdateSpecialtyCommand(String name, Integer durationMinutes, Boolean active) {
}
