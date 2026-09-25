package com.fcv.citas.scheduling.infrastructure.web;

import com.fcv.citas.scheduling.domain.port.in.AvailabilityQueryUseCase;
import com.fcv.citas.scheduling.infrastructure.web.dto.AvailableProfessionalResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/** Búsqueda de disponibilidad del USER (HU-014): solo horarios que completan la duración de la especialidad. */
@RestController
@RequestMapping("/api/v1/availability")
public class AvailabilityController {

    private final AvailabilityQueryUseCase availability;

    public AvailabilityController(AvailabilityQueryUseCase availability) {
        this.availability = availability;
    }

    @GetMapping
    public List<AvailableProfessionalResponse> search(
            @RequestParam Long locationId,
            @RequestParam Long specialtyId,
            @RequestParam(required = false) Long professionalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return availability.search(locationId, specialtyId, professionalId, date).stream()
                .map(AvailableProfessionalResponse::from)
                .toList();
    }
}
