package com.fcv.citas.catalog.domain.port.in;

import com.fcv.citas.catalog.domain.model.Specialty;

public interface UpdateSpecialtyUseCase {
    Specialty update(Long id, UpdateSpecialtyCommand command);
}
