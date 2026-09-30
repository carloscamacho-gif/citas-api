package com.fcv.citas.catalog.domain.port.in;

/** Datos para crear o actualizar una EPS. En actualización, {@code code} se ignora (es inmutable). */
public record EpsCommand(String code, String name, Boolean active) {
}
