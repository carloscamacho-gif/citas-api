package com.fcv.citas.catalog.domain.port.out;

import com.fcv.citas.catalog.domain.model.InsuranceRegime;

import java.util.List;

public interface InsuranceRegimeRepositoryPort {

    List<InsuranceRegime> findAll();

    boolean exists(Long id);
}
