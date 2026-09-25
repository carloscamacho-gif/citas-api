package com.fcv.citas.scheduling.infrastructure.persistence.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="availability_blocks")
public class AvailabilityBlockJpaEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="professional_id") private Long professionalId; @Column(name="location_id") private Long locationId;
 @Column(name="start_at") private LocalDateTime startAt; @Column(name="end_at") private LocalDateTime endAt; private boolean active;
 protected AvailabilityBlockJpaEntity(){} public AvailabilityBlockJpaEntity(Long id,Long p,Long l,LocalDateTime s,LocalDateTime e,boolean a){this.id=id;professionalId=p;locationId=l;startAt=s;endAt=e;active=a;}
 public Long getId(){return id;} public Long getProfessionalId(){return professionalId;} public Long getLocationId(){return locationId;} public LocalDateTime getStartAt(){return startAt;} public LocalDateTime getEndAt(){return endAt;} public boolean isActive(){return active;}
}
