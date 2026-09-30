package com.fcv.citas.scheduling.infrastructure.persistence.repository;

import com.fcv.citas.scheduling.infrastructure.persistence.entity.RescheduleRequestJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataRescheduleRepository extends JpaRepository<RescheduleRequestJpaEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RescheduleRequestJpaEntity r where r.id = :id")
    Optional<RescheduleRequestJpaEntity> findByIdForUpdate(@Param("id") Long id);

    boolean existsByAppointmentIdAndStatusId(Long appointmentId, Long statusId);

    /** Bandeja PENDING con filtros (RF-18). profesional/especialidad viven en la cita: join implícito. */
    @Query("""
            select r from RescheduleRequestJpaEntity r, AppointmentJpaEntity a
            where r.appointmentId = a.id
              and r.statusId = :statusId
              and (:locationId is null or r.requestedLocationId = :locationId)
              and (:professionalId is null or a.professionalId = :professionalId)
              and (:specialtyId is null or a.specialtyId = :specialtyId)
              and r.requestedStartAt >= :from and r.requestedStartAt < :to
            order by r.requestedStartAt, r.id
            """)
    List<RescheduleRequestJpaEntity> search(@Param("statusId") Long statusId, @Param("locationId") Long locationId,
                                            @Param("professionalId") Long professionalId,
                                            @Param("specialtyId") Long specialtyId,
                                            @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
