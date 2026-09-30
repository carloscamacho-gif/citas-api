package com.fcv.citas.catalog.domain.port.in;

import com.fcv.citas.catalog.domain.model.Eps;

import java.util.List;

/** HU-007: CRUD de EPS para ADMIN. El borrado físico solo procede si la EPS no está referenciada (RF-06). */
public interface AdminEpsUseCase {
    List<Eps> list();

    Eps create(EpsCommand command);

    Eps update(Long id, EpsCommand command);

    void delete(Long id);
}
