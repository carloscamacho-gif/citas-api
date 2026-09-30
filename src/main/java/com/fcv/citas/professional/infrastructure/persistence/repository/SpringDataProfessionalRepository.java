package com.fcv.citas.professional.infrastructure.persistence.repository;

import com.fcv.citas.professional.infrastructure.persistence.entity.ProfessionalJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpringDataProfessionalRepository extends JpaRepository<ProfessionalJpaEntity, Long> {
    Optional<ProfessionalJpaEntity> findByUser_Id(Long userId);
    boolean existsByProfessionalCode(String code);
    boolean existsByLicenseNumber(String licenseNumber);
}
