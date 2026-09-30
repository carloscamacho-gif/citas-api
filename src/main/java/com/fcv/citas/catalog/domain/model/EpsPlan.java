package com.fcv.citas.catalog.domain.model;

/**
 * Plan de una EPS (HU-008). Es el punto de referencia único desde el que se derivan EPS y régimen al
 * afiliar un usuario (README_DB, RF-04). Lleva los nombres de EPS/régimen para mostrarlos en la interfaz.
 */
public record EpsPlan(Long id, Long epsId, String epsName, Long regimeId, String regimeName,
                      String code, String name, boolean active) {
}
