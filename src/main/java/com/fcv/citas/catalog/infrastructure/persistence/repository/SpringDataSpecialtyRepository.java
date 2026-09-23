package com.fcv.citas.catalog.infrastructure.persistence.repository;

import com.fcv.citas.catalog.infrastructure.persistence.entity.SpecialtyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataSpecialtyRepository extends JpaRepository<SpecialtyJpaEntity, Long> {

    boolean existsByCode(String code);

    List<SpecialtyJpaEntity> findByActiveTrue();
}
