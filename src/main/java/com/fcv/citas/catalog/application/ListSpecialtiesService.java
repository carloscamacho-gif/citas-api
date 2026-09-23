package com.fcv.citas.catalog.application;

import com.fcv.citas.catalog.domain.model.Specialty;
import com.fcv.citas.catalog.domain.port.in.ListSpecialtiesUseCase;
import com.fcv.citas.catalog.domain.port.out.SpecialtyRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListSpecialtiesService implements ListSpecialtiesUseCase {

    private final SpecialtyRepositoryPort repository;

    public ListSpecialtiesService(SpecialtyRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public List<Specialty> listAll() {
        return repository.findAll();
    }

    @Override
    public List<Specialty> listActive() {
        return repository.findActive();
    }
}
