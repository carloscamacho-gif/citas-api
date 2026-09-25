package com.fcv.citas.professional.domain.port.out;

import com.fcv.citas.professional.domain.model.Professional;
import java.util.List;
import java.util.Optional;

public interface ProfessionalRepositoryPort {
    Professional save(Professional professional);
    Optional<Professional> findById(Long id);
    Optional<Professional> findByUserId(Long userId);
    List<Professional> findAll();
    boolean existsByProfessionalCode(String code);
    boolean existsByLicenseNumber(String licenseNumber);
}
