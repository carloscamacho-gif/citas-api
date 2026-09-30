package com.fcv.citas.catalog.infrastructure.web.dto;

/** Cuerpo de PATCH /api/v1/admin/specialties/{id}. Campos opcionales (null = no cambiar). */
public record UpdateSpecialtyRequest(String name, Integer durationMinutes, Boolean active) {
}
