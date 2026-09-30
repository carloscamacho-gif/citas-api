package com.fcv.citas.catalog.domain.port.out;

import com.fcv.citas.catalog.domain.model.Eps;

import java.util.List;
import java.util.Optional;

public interface EpsRepositoryPort {

    List<Eps> findAll();

    Optional<Eps> findById(Long id);

    boolean existsByCode(String code);

    boolean exists(Long id);

    /** Crea o actualiza; devuelve la EPS con su id asignado. */
    Eps save(Eps eps);

    /** ¿La EPS está referenciada por algún plan? Si lo está, no puede borrarse físicamente (solo desactivarse). */
    boolean isReferenced(Long id);

    void delete(Long id);
}
