package com.fcv.citas.professional.infrastructure.persistence;

import com.fcv.citas.auth.infrastructure.persistence.entity.UserJpaEntity;
import com.fcv.citas.auth.infrastructure.persistence.repository.SpringDataUserRepository;
import com.fcv.citas.professional.domain.model.Professional;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import com.fcv.citas.professional.infrastructure.persistence.entity.ProfessionalJpaEntity;
import com.fcv.citas.professional.infrastructure.persistence.entity.ProfessionalLocationValue;
import com.fcv.citas.professional.infrastructure.persistence.entity.ProfessionalSpecialtyValue;
import com.fcv.citas.professional.infrastructure.persistence.repository.SpringDataProfessionalRepository;
import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProfessionalRepositoryAdapter implements ProfessionalRepositoryPort {
    private final SpringDataProfessionalRepository repository;
    private final SpringDataUserRepository users;
    public ProfessionalRepositoryAdapter(SpringDataProfessionalRepository repository, SpringDataUserRepository users) {
        this.repository=repository; this.users=users;
    }
    @Override public Professional save(Professional p) {
        UserJpaEntity user=users.findById(p.userId()).orElseThrow();
        var specs=p.specialtyIds().stream().map(id -> new ProfessionalSpecialtyValue(id,id.equals(p.primarySpecialtyId()))).collect(Collectors.toSet());
        var locs=p.locationIds().stream().map(ProfessionalLocationValue::new).collect(Collectors.toSet());
        return map(repository.save(new ProfessionalJpaEntity(p.id(),user,p.professionalCode(),p.licenseNumber(),p.active(),specs,locs)));
    }
    @Override public Optional<Professional> findById(Long id){return repository.findById(id).map(this::map);}
    @Override public Optional<Professional> findByUserId(Long id){return repository.findByUser_Id(id).map(this::map);}
    @Override public List<Professional> findAll(){return repository.findAll().stream().map(this::map).toList();}
    @Override public boolean existsByProfessionalCode(String code){return repository.existsByProfessionalCode(code);}
    @Override public boolean existsByLicenseNumber(String value){return repository.existsByLicenseNumber(value);}
    private Professional map(ProfessionalJpaEntity e){
        List<Long> spec=e.getSpecialties().stream().map(ProfessionalSpecialtyValue::getSpecialtyId).sorted().toList();
        Long primary=e.getSpecialties().stream().filter(ProfessionalSpecialtyValue::isPrimary).map(ProfessionalSpecialtyValue::getSpecialtyId).findFirst().orElse(null);
        List<Long> loc=e.getLocations().stream().map(ProfessionalLocationValue::getLocationId).sorted(Comparator.naturalOrder()).toList();
        var u=e.getUser();
        return new Professional(e.getId(),u.getId(),u.getFirstName(),u.getLastName(),u.getEmail(),e.getProfessionalCode(),e.getLicenseNumber(),e.isActive(),spec,primary,loc);
    }
}
