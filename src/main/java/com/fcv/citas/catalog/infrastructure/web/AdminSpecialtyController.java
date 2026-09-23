package com.fcv.citas.catalog.infrastructure.web;

import com.fcv.citas.catalog.domain.model.Specialty;
import com.fcv.citas.catalog.domain.port.in.CreateSpecialtyCommand;
import com.fcv.citas.catalog.domain.port.in.CreateSpecialtyUseCase;
import com.fcv.citas.catalog.domain.port.in.ListSpecialtiesUseCase;
import com.fcv.citas.catalog.domain.port.in.UpdateSpecialtyCommand;
import com.fcv.citas.catalog.domain.port.in.UpdateSpecialtyUseCase;
import com.fcv.citas.catalog.infrastructure.web.dto.CreateSpecialtyRequest;
import com.fcv.citas.catalog.infrastructure.web.dto.SpecialtyResponse;
import com.fcv.citas.catalog.infrastructure.web.dto.UpdateSpecialtyRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** CRUD de especialidades para ADMIN (HU-009). Autorización por rol en SecurityConfig (/admin/**). */
@RestController
@RequestMapping("/api/v1/admin/specialties")
public class AdminSpecialtyController {

    private final ListSpecialtiesUseCase listSpecialties;
    private final CreateSpecialtyUseCase createSpecialty;
    private final UpdateSpecialtyUseCase updateSpecialty;

    public AdminSpecialtyController(ListSpecialtiesUseCase listSpecialties, CreateSpecialtyUseCase createSpecialty,
                                     UpdateSpecialtyUseCase updateSpecialty) {
        this.listSpecialties = listSpecialties;
        this.createSpecialty = createSpecialty;
        this.updateSpecialty = updateSpecialty;
    }

    /** ADMIN ve todas (activas e inactivas). */
    @GetMapping
    public List<SpecialtyResponse> list() {
        return listSpecialties.listAll().stream().map(SpecialtyResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SpecialtyResponse create(@Valid @RequestBody CreateSpecialtyRequest request) {
        Specialty created = createSpecialty.create(new CreateSpecialtyCommand(
                request.code(), request.name(), request.durationMinutes(), request.general()));
        return SpecialtyResponse.from(created);
    }

    @PatchMapping("/{id}")
    public SpecialtyResponse update(@PathVariable Long id, @RequestBody UpdateSpecialtyRequest request) {
        Specialty updated = updateSpecialty.update(id,
                new UpdateSpecialtyCommand(request.name(), request.durationMinutes(), request.active()));
        return SpecialtyResponse.from(updated);
    }
}
