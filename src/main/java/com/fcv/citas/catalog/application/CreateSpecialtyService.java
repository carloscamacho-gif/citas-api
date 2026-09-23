package com.fcv.citas.catalog.application;

import com.fcv.citas.catalog.domain.exception.SpecialtyCodeAlreadyUsedException;
import com.fcv.citas.catalog.domain.model.Specialty;
import com.fcv.citas.catalog.domain.port.in.CreateSpecialtyCommand;
import com.fcv.citas.catalog.domain.port.in.CreateSpecialtyUseCase;
import com.fcv.citas.catalog.domain.port.out.SpecialtyRepositoryPort;
import org.springframework.stereotype.Service;

/** Implementa HU-009 (alta de especialidad). */
@Service
public class CreateSpecialtyService implements CreateSpecialtyUseCase {

    private final SpecialtyRepositoryPort repository;

    public CreateSpecialtyService(SpecialtyRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Specialty create(CreateSpecialtyCommand command) {
        if (repository.existsByCode(command.code())) {
            throw new SpecialtyCodeAlreadyUsedException(command.code());
        }
        // Specialty.create valida la duración (30/60) en el dominio y lanza si es inválida.
        Specialty specialty = Specialty.create(command.code(), command.name(), command.durationMinutes(), command.general());
        return repository.save(specialty);
    }
}
