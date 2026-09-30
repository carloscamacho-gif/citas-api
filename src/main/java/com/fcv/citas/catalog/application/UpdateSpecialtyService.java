package com.fcv.citas.catalog.application;

import com.fcv.citas.catalog.domain.exception.SpecialtyNotFoundException;
import com.fcv.citas.catalog.domain.model.Specialty;
import com.fcv.citas.catalog.domain.port.in.UpdateSpecialtyCommand;
import com.fcv.citas.catalog.domain.port.in.UpdateSpecialtyUseCase;
import com.fcv.citas.catalog.domain.port.out.SpecialtyRepositoryPort;
import org.springframework.stereotype.Service;

/**
 * Implementa HU-009 (actualización parcial + activar/desactivar). No hay borrado físico:
 * desactivar es la única forma de retirar una especialidad referenciada (RF-06, CA-03).
 */
@Service
public class UpdateSpecialtyService implements UpdateSpecialtyUseCase {

    private final SpecialtyRepositoryPort repository;

    public UpdateSpecialtyService(SpecialtyRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Specialty update(Long id, UpdateSpecialtyCommand command) {
        Specialty current = repository.findById(id).orElseThrow(() -> new SpecialtyNotFoundException(id));

        String name = command.name() != null ? command.name() : current.getName();
        int duration = command.durationMinutes() != null ? command.durationMinutes() : current.getDurationMinutes();
        boolean active = command.active() != null ? command.active() : current.isActive();

        // withUpdates revalida la duración (30/60) en el dominio.
        Specialty updated = current.withUpdates(name, duration, active);
        return repository.save(updated);
    }
}
