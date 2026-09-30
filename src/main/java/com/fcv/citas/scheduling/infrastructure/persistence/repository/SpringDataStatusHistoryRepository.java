package com.fcv.citas.scheduling.infrastructure.persistence.repository;

import com.fcv.citas.scheduling.infrastructure.persistence.entity.StatusHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataStatusHistoryRepository extends JpaRepository<StatusHistoryJpaEntity, Long> {

    List<StatusHistoryJpaEntity> findByAppointmentIdOrderByChangedAtAscIdAsc(Long appointmentId);
}
