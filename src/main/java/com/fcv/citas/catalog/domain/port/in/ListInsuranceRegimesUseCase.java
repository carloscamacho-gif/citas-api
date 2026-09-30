package com.fcv.citas.catalog.domain.port.in;

import com.fcv.citas.catalog.domain.model.InsuranceRegime;

import java.util.List;

/** Lista los regímenes de aseguramiento (apoyo a la creación de planes en HU-008). */
public interface ListInsuranceRegimesUseCase {
    List<InsuranceRegime> list();
}
