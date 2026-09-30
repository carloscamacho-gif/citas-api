package com.fcv.citas.scheduling.infrastructure.persistence.repository;

import com.fcv.citas.scheduling.infrastructure.persistence.entity.AppointmentStatusJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAppointmentStatusRepository extends JpaRepository<AppointmentStatusJpaEntity, Long> {
}
