package com.fcv.citas.catalog.domain.port.in;

import com.fcv.citas.catalog.domain.model.Specialty;

import java.util.List;

public interface ListSpecialtiesUseCase {

    /** Todas las especialidades (uso ADMIN). */
    List<Specialty> listAll();

    /** Solo activas (consumo del USER al buscar disponibilidad). */
    List<Specialty> listActive();
}
