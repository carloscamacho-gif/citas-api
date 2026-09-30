package com.fcv.citas.scheduling.infrastructure.persistence.repository;
import com.fcv.citas.scheduling.infrastructure.persistence.entity.AvailabilitySlotJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
public interface SpringDataAvailabilitySlotRepository extends JpaRepository<AvailabilitySlotJpaEntity,Long>{
 List<AvailabilitySlotJpaEntity> findByBlockIdInAndAppointmentIdIsNullOrderByStartAt(List<Long> blockIds);
 boolean existsByBlockIdAndAppointmentIdIsNotNull(Long blockId);
 void deleteByBlockId(Long blockId);
 List<AvailabilitySlotJpaEntity> findByAppointmentId(Long appointmentId);
 @Query("select s from AvailabilitySlotJpaEntity s where s.appointmentId=:appt and s.startAt>=:start and s.startAt<:end") List<AvailabilitySlotJpaEntity> findByAppointmentIdInRange(@Param("appt")Long appt,@Param("start")LocalDateTime start,@Param("end")LocalDateTime end);
 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("select s from AvailabilitySlotJpaEntity s where s.blockId=:block and s.startAt in :starts order by s.startAt") List<AvailabilitySlotJpaEntity> lockSlots(@Param("block")Long block,@Param("starts")List<LocalDateTime> starts);
}
