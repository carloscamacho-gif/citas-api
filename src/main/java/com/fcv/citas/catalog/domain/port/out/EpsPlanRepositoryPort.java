package com.fcv.citas.catalog.domain.port.out;

import com.fcv.citas.catalog.domain.model.EpsPlan;

import java.util.List;
import java.util.Optional;

public interface EpsPlanRepositoryPort {

    List<EpsPlan> findAll();

    Optional<EpsPlan> findById(Long id);

    boolean existsByCode(String code);

    /** Crea o actualiza; devuelve el plan con su id asignado y los nombres de EPS/régimen resueltos. */
    EpsPlan save(EpsPlan plan);

    /** ¿El plan está referenciado por alguna afiliación? Si lo está, no puede borrarse físicamente. */
    boolean isReferenced(Long id);

    void delete(Long id);
}
