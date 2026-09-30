package com.fcv.citas.professional.domain.port.in;

import com.fcv.citas.professional.domain.model.Professional;
import java.util.List;

public interface ProfessionalManagementUseCase {
    Professional create(CreateProfessionalCommand command);
    List<Professional> list();
    Professional setActive(Long id, boolean active);
    Professional assignSpecialties(Long id, List<Long> specialtyIds, Long primarySpecialtyId);
    Professional assignLocations(Long id, List<Long> locationIds);
}
