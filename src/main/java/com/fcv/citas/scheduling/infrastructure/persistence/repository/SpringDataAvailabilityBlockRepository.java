package com.fcv.citas.scheduling.infrastructure.persistence.repository;
import com.fcv.citas.scheduling.infrastructure.persistence.entity.AvailabilityBlockJpaEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
public interface SpringDataAvailabilityBlockRepository extends JpaRepository<AvailabilityBlockJpaEntity,Long>{
 @Query("select b from AvailabilityBlockJpaEntity b where b.professionalId=:p and b.active=true and (:location is null or b.locationId=:location) and (:from is null or b.endAt>:from) and (:to is null or b.startAt<:to) order by b.startAt") List<AvailabilityBlockJpaEntity> search(@Param("p")Long p,@Param("from")LocalDateTime from,@Param("to")LocalDateTime to,@Param("location")Long location);
 @Query("select count(b)>0 from AvailabilityBlockJpaEntity b where b.professionalId=:p and b.active=true and b.startAt<:end and b.endAt>:start and (:excluded is null or b.id<>:excluded)") boolean overlaps(@Param("p")Long p,@Param("start")LocalDateTime start,@Param("end")LocalDateTime end,@Param("excluded")Long excluded);
 @Query("select b from AvailabilityBlockJpaEntity b where b.professionalId=:p and b.locationId=:l and b.active=true and b.startAt<=:start and b.endAt>=:end") List<AvailabilityBlockJpaEntity> containing(@Param("p")Long p,@Param("l")Long l,@Param("start")LocalDateTime start,@Param("end")LocalDateTime end);
}
