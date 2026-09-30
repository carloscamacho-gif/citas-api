package com.fcv.citas.catalog.domain.port.in;

/**
 * Datos para crear o actualizar un plan de EPS. En actualización, {@code code}, {@code epsId} y
 * {@code regimeId} se ignoran (inmutables: el plan es el punto único desde el que se derivan EPS y régimen).
 */
public record EpsPlanCommand(Long epsId, Long regimeId, String code, String name, Boolean active) {
}
