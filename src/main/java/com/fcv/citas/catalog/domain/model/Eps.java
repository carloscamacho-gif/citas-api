package com.fcv.citas.catalog.domain.model;

/** EPS del catálogo de aseguramiento (HU-007). El borrado físico se sustituye por desactivación. */
public record Eps(Long id, String code, String name, boolean active) {
}
