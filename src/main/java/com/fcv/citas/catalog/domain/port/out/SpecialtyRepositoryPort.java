package com.fcv.citas.catalog.domain.port.out;

import com.fcv.citas.catalog.domain.model.Specialty;

import java.util.List;
import java.util.Optional;

public interface SpecialtyRepositoryPort {

    List<Specialty> findAll();

    /** Solo las especialidades activas (para catálogos de consumo del USER). */
    List<Specialty> findActive();

    Optional<Specialty> findById(Long id);

    boolean existsByCode(String code);

    Specialty save(Specialty specialty);
}
