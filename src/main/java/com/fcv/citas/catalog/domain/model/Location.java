package com.fcv.citas.catalog.domain.model;

/** Sede fija del laboratorio (RF-05). Solo lectura desde la aplicación. */
public record Location(Long id, String code, String name, boolean active) {
}
