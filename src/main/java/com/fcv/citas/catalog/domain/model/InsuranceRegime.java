package com.fcv.citas.catalog.domain.model;

/** Régimen de aseguramiento (p. ej. Contributivo). Catálogo fijo de referencia para los planes. */
public record InsuranceRegime(Long id, String code, String name) {
}
