package com.fcv.citas.catalog.domain.port.out;

import com.fcv.citas.catalog.domain.model.Location;

import java.util.List;
import java.util.Optional;

public interface LocationRepositoryPort {

    List<Location> findAll();

    Optional<Location> findById(Long id);
}
