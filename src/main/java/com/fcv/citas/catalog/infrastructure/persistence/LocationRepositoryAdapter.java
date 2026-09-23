package com.fcv.citas.catalog.infrastructure.persistence;

import com.fcv.citas.catalog.domain.model.Location;
import com.fcv.citas.catalog.domain.port.out.LocationRepositoryPort;
import com.fcv.citas.catalog.infrastructure.persistence.entity.LocationJpaEntity;
import com.fcv.citas.catalog.infrastructure.persistence.repository.SpringDataLocationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class LocationRepositoryAdapter implements LocationRepositoryPort {

    private final SpringDataLocationRepository repository;

    public LocationRepositoryAdapter(SpringDataLocationRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Location> findAll() {
        return repository.findAll().stream().map(LocationRepositoryAdapter::toDomain).toList();
    }

    @Override
    public Optional<Location> findById(Long id) {
        return repository.findById(id).map(LocationRepositoryAdapter::toDomain);
    }

    static Location toDomain(LocationJpaEntity e) {
        return new Location(e.getId(), e.getCode(), e.getName(), e.isActive());
    }
}
