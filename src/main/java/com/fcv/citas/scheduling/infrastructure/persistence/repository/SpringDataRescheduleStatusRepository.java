package com.fcv.citas.scheduling.infrastructure.persistence.repository;

import com.fcv.citas.scheduling.infrastructure.persistence.entity.RescheduleStatusJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataRescheduleStatusRepository extends JpaRepository<RescheduleStatusJpaEntity, Long> {
}
