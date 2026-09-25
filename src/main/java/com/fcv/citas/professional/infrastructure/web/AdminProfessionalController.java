package com.fcv.citas.professional.infrastructure.web;

import com.fcv.citas.professional.domain.port.in.CreateProfessionalCommand;
import com.fcv.citas.professional.domain.port.in.ProfessionalManagementUseCase;
import com.fcv.citas.professional.infrastructure.web.dto.CreateProfessionalRequest;
import com.fcv.citas.professional.infrastructure.web.dto.ProfessionalResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/professionals")
public class AdminProfessionalController {
    private final ProfessionalManagementUseCase service;
    public AdminProfessionalController(ProfessionalManagementUseCase service){this.service=service;}
    @GetMapping public List<ProfessionalResponse> list(){return service.list().stream().map(ProfessionalResponse::from).toList();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ProfessionalResponse create(@Valid @RequestBody CreateProfessionalRequest r){
        return ProfessionalResponse.from(service.create(new CreateProfessionalCommand(r.firstName(),r.lastName(),r.documentType(),r.documentNumber(),r.email(),r.phone(),r.password(),r.professionalCode(),r.licenseNumber(),r.specialtyIds(),r.primarySpecialtyId(),r.locationIds())));
    }
    @PatchMapping("/{id}/active") public ProfessionalResponse active(@PathVariable Long id,@Valid @RequestBody ActiveRequest r){return ProfessionalResponse.from(service.setActive(id,r.active()));}
    @PutMapping("/{id}/specialties") public ProfessionalResponse specialties(@PathVariable Long id,@Valid @RequestBody SpecialtiesRequest r){return ProfessionalResponse.from(service.assignSpecialties(id,r.specialtyIds(),r.primarySpecialtyId()));}
    @PutMapping("/{id}/locations") public ProfessionalResponse locations(@PathVariable Long id,@Valid @RequestBody LocationsRequest r){return ProfessionalResponse.from(service.assignLocations(id,r.locationIds()));}
    public record ActiveRequest(boolean active){}
    public record SpecialtiesRequest(@NotEmpty List<Long> specialtyIds,@NotNull Long primarySpecialtyId){}
    public record LocationsRequest(@NotEmpty List<Long> locationIds){}
}
