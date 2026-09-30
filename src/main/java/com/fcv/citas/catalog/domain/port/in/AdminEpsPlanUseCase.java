package com.fcv.citas.catalog.domain.port.in;

import com.fcv.citas.catalog.domain.model.EpsPlan;

import java.util.List;

/** HU-008: CRUD de planes de EPS para ADMIN. El borrado físico solo procede si el plan no tiene afiliaciones (RF-06). */
public interface AdminEpsPlanUseCase {
    List<EpsPlan> list();

    EpsPlan create(EpsPlanCommand command);

    EpsPlan update(Long id, EpsPlanCommand command);

    void delete(Long id);
}
