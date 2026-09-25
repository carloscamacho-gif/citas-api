package com.fcv.citas.scheduling.infrastructure.persistence;
import com.fcv.citas.catalog.domain.port.out.LocationRepositoryPort;
import com.fcv.citas.scheduling.domain.exception.SchedulingConflictException;
import com.fcv.citas.scheduling.domain.model.*;
import com.fcv.citas.scheduling.domain.port.out.SchedulingRepositoryPort;
import com.fcv.citas.scheduling.infrastructure.persistence.entity.*;
import com.fcv.citas.scheduling.infrastructure.persistence.repository.*;
import org.springframework.stereotype.Component;
import java.time.*;
import java.util.*;

@Component
public class SchedulingRepositoryAdapter implements SchedulingRepositoryPort {
 private final SpringDataAvailabilityBlockRepository blocks; private final SpringDataAvailabilitySlotRepository slots; private final LocationRepositoryPort locations;
 public SchedulingRepositoryAdapter(SpringDataAvailabilityBlockRepository b,SpringDataAvailabilitySlotRepository s,LocationRepositoryPort l){blocks=b;slots=s;locations=l;}
 @Override public AvailabilityBlock saveBlock(AvailabilityBlock b){return map(blocks.save(new AvailabilityBlockJpaEntity(b.id(),b.professionalId(),b.locationId(),b.startAt(),b.endAt(),b.active())));}
 @Override public Optional<AvailabilityBlock> findBlock(Long id){return blocks.findById(id).map(this::map);}
 @Override public List<AvailabilityBlock> findBlocks(Long professionalId,LocalDate date,Long locationId){LocalDateTime from=date==null?null:date.atStartOfDay();LocalDateTime to=date==null?null:date.plusDays(1).atStartOfDay();return blocks.search(professionalId,from,to,locationId).stream().map(this::map).toList();}
 @Override public boolean overlaps(Long p,LocalDateTime s,LocalDateTime e,Long x){return blocks.overlaps(p,s,e,x);}
 @Override public boolean hasCommittedSlots(Long id){return slots.existsByBlockIdAndAppointmentIdIsNotNull(id);}
 @Override public void deleteBlock(Long id){slots.deleteByBlockId(id);blocks.deleteById(id);}
 @Override public void replaceSlots(Long id,LocalDateTime start,LocalDateTime end){slots.deleteByBlockId(id);List<AvailabilitySlotJpaEntity> values=new ArrayList<>();for(LocalDateTime t=start;t.isBefore(end);t=t.plusMinutes(30))values.add(new AvailabilitySlotJpaEntity(id,t,t.plusMinutes(30)));slots.saveAll(values);}
 @Override public List<AvailabilitySlot> findFreeSlots(List<Long> ids){if(ids.isEmpty())return List.of();return slots.findByBlockIdInAndAppointmentIdIsNullOrderByStartAt(ids).stream().map(this::map).toList();}
 @Override public List<AvailabilitySlot> lockRequiredSlots(Long professionalId,Long locationId,LocalDateTime startAt,int count){
   LocalDateTime end=startAt.plusMinutes(30L*count);List<AvailabilityBlockJpaEntity> candidates=blocks.containing(professionalId,locationId,startAt,end);if(candidates.isEmpty())throw new SchedulingConflictException("El horario no pertenece a un bloque disponible");
   Long blockId=candidates.getFirst().getId();List<LocalDateTime> starts=new ArrayList<>();for(int i=0;i<count;i++)starts.add(startAt.plusMinutes(30L*i));List<AvailabilitySlotJpaEntity> locked=slots.lockSlots(blockId,starts);
   if(locked.size()!=count||locked.stream().anyMatch(s->s.getAppointmentId()!=null))throw new SchedulingConflictException("El horario dejó de estar disponible");return locked.stream().map(this::map).toList();
 }
 @Override public void assignSlots(List<Long> ids,Long appointmentId){List<AvailabilitySlotJpaEntity> values=slots.findAllById(ids);values.forEach(s->s.assign(appointmentId));slots.saveAll(values);}
 @Override public void releaseSlots(Long appointmentId){List<AvailabilitySlotJpaEntity> values=slots.findByAppointmentId(appointmentId);values.forEach(s->s.assign(null));slots.saveAll(values);}
 private AvailabilityBlock map(AvailabilityBlockJpaEntity e){String name=locations.findById(e.getLocationId()).map(l->l.name()).orElse("");return new AvailabilityBlock(e.getId(),e.getProfessionalId(),e.getLocationId(),name,e.getStartAt(),e.getEndAt(),e.isActive());}
 private AvailabilitySlot map(AvailabilitySlotJpaEntity e){return new AvailabilitySlot(e.getId(),e.getBlockId(),e.getStartAt(),e.getEndAt(),e.getAppointmentId());}
}
