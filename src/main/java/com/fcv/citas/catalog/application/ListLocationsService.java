package com.fcv.citas.catalog.application;

import com.fcv.citas.catalog.domain.model.Location;
import com.fcv.citas.catalog.domain.port.in.ListLocationsUseCase;
import com.fcv.citas.catalog.domain.port.out.LocationRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListLocationsService implements ListLocationsUseCase {

    private final LocationRepositoryPort repository;

    public ListLocationsService(LocationRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public List<Location> list() {
        return repository.findAll();
    }
}
