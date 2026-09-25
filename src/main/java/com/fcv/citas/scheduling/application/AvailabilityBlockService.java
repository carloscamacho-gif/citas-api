package com.fcv.citas.scheduling.application;

import com.fcv.citas.catalog.domain.port.out.LocationRepositoryPort;
import com.fcv.citas.professional.domain.exception.ProfessionalNotFoundException;
import com.fcv.citas.professional.domain.model.Professional;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import com.fcv.citas.scheduling.domain.exception.AvailabilityBlockNotFoundException;
import com.fcv.citas.scheduling.domain.exception.InvalidSchedulingException;
import com.fcv.citas.scheduling.domain.exception.SchedulingConflictException;
import com.fcv.citas.scheduling.domain.model.AvailabilityBlock;
import com.fcv.citas.scheduling.domain.port.in.AvailabilityBlockCommand;
import com.fcv.citas.scheduling.domain.port.in.AvailabilityBlockUseCase;
import com.fcv.citas.scheduling.domain.port.out.SchedulingRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AvailabilityBlockService implements AvailabilityBlockUseCase {
    private final ProfessionalRepositoryPort professionals; private final LocationRepositoryPort locations;
    private final SchedulingRepositoryPort scheduling; private final Clock clock;
    public AvailabilityBlockService(ProfessionalRepositoryPort professionals, LocationRepositoryPort locations,
            SchedulingRepositoryPort scheduling, Clock clock){this.professionals=professionals;this.locations=locations;this.scheduling=scheduling;this.clock=clock;}
    @Override @Transactional public AvailabilityBlock create(Long userId, AvailabilityBlockCommand command){
        Professional p=requireProfessional(userId); validate(p,command,null);
        AvailabilityBlock saved=scheduling.saveBlock(new AvailabilityBlock(null,p.id(),command.locationId(),null,command.startAt(),command.endAt(),true));
        scheduling.replaceSlots(saved.id(),saved.startAt(),saved.endAt()); return saved;
    }
    @Override @Transactional public AvailabilityBlock update(Long userId,Long blockId,AvailabilityBlockCommand command){
        Professional p=requireProfessional(userId); AvailabilityBlock current=requireOwned(p,blockId);
        if(scheduling.hasCommittedSlots(blockId)) throw new SchedulingConflictException("El bloque tiene citas comprometidas");
        validate(p,command,blockId);
        AvailabilityBlock saved=scheduling.saveBlock(new AvailabilityBlock(blockId,p.id(),command.locationId(),null,command.startAt(),command.endAt(),current.active()));
        scheduling.replaceSlots(blockId,saved.startAt(),saved.endAt()); return saved;
    }
    @Override @Transactional public void delete(Long userId,Long blockId){
        Professional p=requireProfessional(userId); AvailabilityBlock block=requireOwned(p,blockId);
        if(block.startAt().isBefore(LocalDateTime.now(clock))) throw new InvalidSchedulingException("Solo se eliminan bloques futuros");
        if(scheduling.hasCommittedSlots(blockId)) throw new SchedulingConflictException("El bloque tiene citas comprometidas");
        scheduling.deleteBlock(blockId);
    }
    @Override public List<AvailabilityBlock> list(Long userId,LocalDate date,Long locationId){return scheduling.findBlocks(requireProfessional(userId).id(),date,locationId);}
    private Professional requireProfessional(Long userId){return professionals.findByUserId(userId).orElseThrow(() -> new ProfessionalNotFoundException(userId));}
    private AvailabilityBlock requireOwned(Professional p,Long id){AvailabilityBlock b=scheduling.findBlock(id).orElseThrow(() -> new AvailabilityBlockNotFoundException(id));if(!b.professionalId().equals(p.id()))throw new InvalidSchedulingException("El bloque pertenece a otro profesional");return b;}
    private void validate(Professional p,AvailabilityBlockCommand c,Long excluded){
        if(!p.active())throw new InvalidSchedulingException("El profesional está inactivo");
        if(c.locationId()==null||!p.locationIds().contains(c.locationId()))throw new InvalidSchedulingException("El profesional no está habilitado en la sede");
        if(locations.findById(c.locationId()).filter(l->l.active()).isEmpty())throw new InvalidSchedulingException("La sede no existe o está inactiva");
        if(c.startAt()==null||c.endAt()==null||!c.endAt().isAfter(c.startAt()))throw new InvalidSchedulingException("El rango horario no es válido");
        if(!c.startAt().isAfter(LocalDateTime.now(clock)))throw new InvalidSchedulingException("No se permiten bloques en el pasado");
        if(Duration.between(c.startAt(),c.endAt()).toMinutes()%30!=0)throw new InvalidSchedulingException("El bloque debe dividirse exactamente en slots de 30 minutos");
        if(scheduling.overlaps(p.id(),c.startAt(),c.endAt(),excluded))throw new SchedulingConflictException("El bloque se solapa con otro horario");
    }
}
