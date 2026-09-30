package com.fcv.citas.scheduling.application;

import com.fcv.citas.catalog.domain.exception.SpecialtyNotFoundException;
import com.fcv.citas.catalog.domain.model.Specialty;
import com.fcv.citas.catalog.domain.port.out.LocationRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.SpecialtyRepositoryPort;
import com.fcv.citas.professional.domain.model.Professional;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import com.fcv.citas.scheduling.domain.exception.InvalidSchedulingException;
import com.fcv.citas.scheduling.domain.model.AvailabilityBlock;
import com.fcv.citas.scheduling.domain.model.AvailabilitySlot;
import com.fcv.citas.scheduling.domain.model.AvailableProfessional;
import com.fcv.citas.scheduling.domain.port.in.AvailabilityQueryUseCase;
import com.fcv.citas.scheduling.domain.port.out.SchedulingRepositoryPort;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AvailabilityQueryService implements AvailabilityQueryUseCase {
    private final ProfessionalRepositoryPort professionals; private final SpecialtyRepositoryPort specialties;
    private final LocationRepositoryPort locations; private final SchedulingRepositoryPort scheduling;
    public AvailabilityQueryService(ProfessionalRepositoryPort p,SpecialtyRepositoryPort s,LocationRepositoryPort l,SchedulingRepositoryPort r){professionals=p;specialties=s;locations=l;scheduling=r;}
    @Override public List<AvailableProfessional> search(Long locationId,Long specialtyId,Long professionalId,LocalDate date){
        Specialty specialty=specialties.findById(specialtyId).orElseThrow(()->new SpecialtyNotFoundException(specialtyId));
        if(!specialty.isActive())throw new InvalidSchedulingException("La especialidad está inactiva");
        if(locations.findById(locationId).filter(l->l.active()).isEmpty())throw new InvalidSchedulingException("La sede no existe o está inactiva");
        List<AvailableProfessional> result=new ArrayList<>();
        for(Professional p:professionals.findAll()){
            if((professionalId!=null&&!professionalId.equals(p.id()))||!p.supports(specialtyId,locationId))continue;
            List<AvailabilityBlock> blocks=scheduling.findBlocks(p.id(),date,locationId);
            List<AvailabilitySlot> free=scheduling.findFreeSlots(blocks.stream().map(AvailabilityBlock::id).toList());
            List<AvailableProfessional.Window> windows=windows(free,specialty.getDurationMinutes());
            if(!windows.isEmpty())result.add(new AvailableProfessional(p.id(),p.fullName(),windows));
        }
        return result;
    }
    static List<AvailableProfessional.Window> windows(List<AvailabilitySlot> slots,int duration){
        Map<Long,List<AvailabilitySlot>> byBlock=slots.stream().collect(Collectors.groupingBy(AvailabilitySlot::blockId));
        List<AvailableProfessional.Window> result=new ArrayList<>();
        for(List<AvailabilitySlot> group:byBlock.values()){
            List<AvailabilitySlot> ordered=group.stream().sorted(Comparator.comparing(AvailabilitySlot::startAt)).toList();
            for(int i=0;i<ordered.size();i++){
                AvailabilitySlot first=ordered.get(i);
                if(duration==30)result.add(new AvailableProfessional.Window(first.startAt(),first.endAt()));
                else if(i+1<ordered.size()&&first.endAt().equals(ordered.get(i+1).startAt()))result.add(new AvailableProfessional.Window(first.startAt(),ordered.get(i+1).endAt()));
            }
        }
        return result.stream().sorted(Comparator.comparing(AvailableProfessional.Window::startAt)).toList();
    }
}
