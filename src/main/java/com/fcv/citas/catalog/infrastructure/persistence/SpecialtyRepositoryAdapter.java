package com.fcv.citas.catalog.infrastructure.persistence;

import com.fcv.citas.catalog.domain.model.Specialty;
import com.fcv.citas.catalog.domain.port.out.SpecialtyRepositoryPort;
import com.fcv.citas.catalog.infrastructure.persistence.entity.SpecialtyJpaEntity;
import com.fcv.citas.catalog.infrastructure.persistence.repository.SpringDataSpecialtyRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SpecialtyRepositoryAdapter implements SpecialtyRepositoryPort {

    private final SpringDataSpecialtyRepository repository;

    public SpecialtyRepositoryAdapter(SpringDataSpecialtyRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Specialty> findAll() {
        return repository.findAll().stream().map(SpecialtyRepositoryAdapter::toDomain).toList();
    }

    @Override
    public List<Specialty> findActive() {
        return repository.findByActiveTrue().stream().map(SpecialtyRepositoryAdapter::toDomain).toList();
    }

    @Override
    public Optional<Specialty> findById(Long id) {
        return repository.findById(id).map(SpecialtyRepositoryAdapter::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return repository.existsByCode(code);
    }

    @Override
    public Specialty save(Specialty specialty) {
        SpecialtyJpaEntity saved = repository.save(new SpecialtyJpaEntity(
                specialty.getId(), specialty.getCode(), specialty.getName(),
                specialty.getDurationMinutes(), specialty.isGeneral(), specialty.isActive()));
        return toDomain(saved);
    }

    static Specialty toDomain(SpecialtyJpaEntity e) {
        return new Specialty(e.getId(), e.getCode(), e.getName(), e.getDurationMinutes(), e.isGeneral(), e.isActive());
    }
}
