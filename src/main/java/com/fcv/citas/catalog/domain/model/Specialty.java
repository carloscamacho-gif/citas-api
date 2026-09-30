package com.fcv.citas.catalog.domain.model;

import com.fcv.citas.catalog.domain.exception.InvalidSpecialtyDurationException;

/**
 * Especialidad configurable (RF-06, RF-09). La duración solo puede ser 30 o 60 minutos y el
 * profesional no la sobrescribe. `general` marca la especialidad de cita general auto-aprobada.
 */
public class Specialty {

    private final Long id;
    private final String code;
    private final String name;
    private final int durationMinutes;
    private final boolean general;
    private final boolean active;

    public Specialty(Long id, String code, String name, int durationMinutes, boolean general, boolean active) {
        if (durationMinutes != 30 && durationMinutes != 60) {
            throw new InvalidSpecialtyDurationException(durationMinutes);
        }
        this.id = id;
        this.code = code;
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.general = general;
        this.active = active;
    }

    /** Alta de una especialidad nueva (nace activa). */
    public static Specialty create(String code, String name, int durationMinutes, boolean general) {
        return new Specialty(null, code, name, durationMinutes, general, true);
    }

    /** Devuelve una copia con los campos editables actualizados (RF-06: sin borrado físico, solo activar/desactivar). */
    public Specialty withUpdates(String newName, int newDurationMinutes, boolean newActive) {
        return new Specialty(id, code, newName, newDurationMinutes, general, newActive);
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public boolean isGeneral() {
        return general;
    }

    public boolean isActive() {
        return active;
    }
}
