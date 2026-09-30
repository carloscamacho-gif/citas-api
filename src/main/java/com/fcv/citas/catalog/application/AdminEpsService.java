package com.fcv.citas.catalog.application;

import com.fcv.citas.catalog.domain.exception.CatalogCodeAlreadyUsedException;
import com.fcv.citas.catalog.domain.exception.CatalogInUseException;
import com.fcv.citas.catalog.domain.exception.EpsNotFoundException;
import com.fcv.citas.catalog.domain.model.Eps;
import com.fcv.citas.catalog.domain.port.in.AdminEpsUseCase;
import com.fcv.citas.catalog.domain.port.in.EpsCommand;
import com.fcv.citas.catalog.domain.port.out.EpsRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Implementa HU-007: CRUD de EPS. El borrado físico se impide si la EPS está referenciada por planes (RF-06). */
@Service
public class AdminEpsService implements AdminEpsUseCase {

    private final EpsRepositoryPort repository;

    public AdminEpsService(EpsRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Eps> list() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Eps create(EpsCommand command) {
        String code = requireText(command.code(), "El código de la EPS es obligatorio");
        String name = requireText(command.name(), "El nombre de la EPS es obligatorio");
        if (repository.existsByCode(code)) {
            throw new CatalogCodeAlreadyUsedException(code);
        }
        boolean active = command.active() == null || command.active();
        return repository.save(new Eps(null, code, name, active));
    }

    @Override
    @Transactional
    public Eps update(Long id, EpsCommand command) {
        Eps current = repository.findById(id).orElseThrow(() -> new EpsNotFoundException(id));
        String name = command.name() == null || command.name().isBlank() ? current.name() : command.name().trim();
        boolean active = command.active() == null ? current.active() : command.active();
        return repository.save(new Eps(id, current.code(), name, active));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.exists(id)) {
            throw new EpsNotFoundException(id);
        }
        // RF-06 / CA-02: una EPS referenciada por planes no se borra físicamente; se desactiva.
        if (repository.isReferenced(id)) {
            throw new CatalogInUseException("La EPS tiene planes asociados; desactívela en lugar de eliminarla");
        }
        repository.delete(id);
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
