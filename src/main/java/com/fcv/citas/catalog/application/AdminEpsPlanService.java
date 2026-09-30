package com.fcv.citas.catalog.application;

import com.fcv.citas.catalog.domain.exception.CatalogCodeAlreadyUsedException;
import com.fcv.citas.catalog.domain.exception.CatalogInUseException;
import com.fcv.citas.catalog.domain.exception.EpsNotFoundException;
import com.fcv.citas.catalog.domain.exception.EpsPlanNotFoundException;
import com.fcv.citas.catalog.domain.model.EpsPlan;
import com.fcv.citas.catalog.domain.port.in.AdminEpsPlanUseCase;
import com.fcv.citas.catalog.domain.port.in.EpsPlanCommand;
import com.fcv.citas.catalog.domain.port.out.EpsPlanRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.EpsRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.InsuranceRegimeRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementa HU-008: CRUD de planes de EPS. Un plan requiere una EPS y un régimen existentes (CA-03);
 * el borrado físico se impide si el plan tiene afiliaciones (RF-06, CA-02).
 */
@Service
public class AdminEpsPlanService implements AdminEpsPlanUseCase {

    private final EpsPlanRepositoryPort plans;
    private final EpsRepositoryPort eps;
    private final InsuranceRegimeRepositoryPort regimes;

    public AdminEpsPlanService(EpsPlanRepositoryPort plans, EpsRepositoryPort eps,
                               InsuranceRegimeRepositoryPort regimes) {
        this.plans = plans;
        this.eps = eps;
        this.regimes = regimes;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EpsPlan> list() {
        return plans.findAll();
    }

    @Override
    @Transactional
    public EpsPlan create(EpsPlanCommand command) {
        String code = requireText(command.code(), "El código del plan es obligatorio");
        String name = requireText(command.name(), "El nombre del plan es obligatorio");
        if (command.epsId() == null || !eps.exists(command.epsId())) {
            // CA-03: no se puede crear un plan para una EPS inexistente.
            throw new EpsNotFoundException(command.epsId());
        }
        if (command.regimeId() == null || !regimes.exists(command.regimeId())) {
            throw new IllegalArgumentException("El régimen indicado no existe");
        }
        if (plans.existsByCode(code)) {
            throw new CatalogCodeAlreadyUsedException(code);
        }
        boolean active = command.active() == null || command.active();
        return plans.save(new EpsPlan(null, command.epsId(), null, command.regimeId(), null, code, name, active));
    }

    @Override
    @Transactional
    public EpsPlan update(Long id, EpsPlanCommand command) {
        EpsPlan current = plans.findById(id).orElseThrow(() -> new EpsPlanNotFoundException(id));
        String name = command.name() == null || command.name().isBlank() ? current.name() : command.name().trim();
        boolean active = command.active() == null ? current.active() : command.active();
        // EPS/régimen/código son inmutables: el plan conserva su vínculo original.
        return plans.save(new EpsPlan(id, current.epsId(), null, current.regimeId(), null, current.code(), name, active));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (plans.findById(id).isEmpty()) {
            throw new EpsPlanNotFoundException(id);
        }
        // RF-06 / CA-02: un plan con afiliaciones no se borra físicamente; se desactiva.
        if (plans.isReferenced(id)) {
            throw new CatalogInUseException("El plan tiene afiliaciones asociadas; desactívelo en lugar de eliminarlo");
        }
        plans.delete(id);
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
