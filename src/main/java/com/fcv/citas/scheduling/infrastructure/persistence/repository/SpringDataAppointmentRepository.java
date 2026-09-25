package com.fcv.citas.scheduling.infrastructure.persistence.repository;

import com.fcv.citas.scheduling.infrastructure.persistence.entity.AppointmentJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataAppointmentRepository extends JpaRepository<AppointmentJpaEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AppointmentJpaEntity a where a.id = :id")
    Optional<AppointmentJpaEntity> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            select a from AppointmentJpaEntity a
            where a.statusId = :statusId
              and (:locationId is null or a.locationId = :locationId)
              and (:professionalId is null or a.professionalId = :professionalId)
              and (:specialtyId is null or a.specialtyId = :specialtyId)
              and a.startAt >= :from and a.startAt < :to
            order by a.startAt, a.id
            """)
    List<AppointmentJpaEntity> search(@Param("statusId") Long statusId, @Param("locationId") Long locationId,
                                      @Param("professionalId") Long professionalId,
                                      @Param("specialtyId") Long specialtyId,
                                      @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
