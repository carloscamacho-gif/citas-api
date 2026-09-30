package com.fcv.citas.scheduling.infrastructure.persistence.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="professional_slots")
public class AvailabilitySlotJpaEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="availability_block_id") private Long blockId; @Column(name="start_at") private LocalDateTime startAt; @Column(name="end_at") private LocalDateTime endAt; @Column(name="appointment_id") private Long appointmentId;
 protected AvailabilitySlotJpaEntity(){} public AvailabilitySlotJpaEntity(Long blockId,LocalDateTime startAt,LocalDateTime endAt){this.blockId=blockId;this.startAt=startAt;this.endAt=endAt;}
 public Long getId(){return id;} public Long getBlockId(){return blockId;} public LocalDateTime getStartAt(){return startAt;} public LocalDateTime getEndAt(){return endAt;} public Long getAppointmentId(){return appointmentId;} public void assign(Long value){appointmentId=value;}
}
