package com.fcv.citas.catalog.application;

import com.fcv.citas.catalog.domain.model.InsuranceRegime;
import com.fcv.citas.catalog.domain.port.in.ListInsuranceRegimesUseCase;
import com.fcv.citas.catalog.domain.port.out.InsuranceRegimeRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lista los regímenes de aseguramiento (apoyo a la creación de planes, HU-008). */
@Service
public class ListInsuranceRegimesService implements ListInsuranceRegimesUseCase {

    private final InsuranceRegimeRepositoryPort regimes;

    public ListInsuranceRegimesService(InsuranceRegimeRepositoryPort regimes) {
        this.regimes = regimes;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsuranceRegime> list() {
        return regimes.findAll();
    }
}
