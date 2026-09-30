package com.fcv.citas.catalog.infrastructure.persistence.repository;

import com.fcv.citas.catalog.infrastructure.persistence.entity.LocationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataLocationRepository extends JpaRepository<LocationJpaEntity, Long> {
}
