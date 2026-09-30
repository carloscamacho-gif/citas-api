package com.fcv.citas.catalog.infrastructure.web;

import com.fcv.citas.catalog.domain.port.in.ListLocationsUseCase;
import com.fcv.citas.catalog.domain.port.in.ListSpecialtiesUseCase;
import com.fcv.citas.catalog.domain.port.in.ListActiveInsurancePlansUseCase;
import com.fcv.citas.catalog.infrastructure.web.dto.InsurancePlanResponse;
import com.fcv.citas.catalog.infrastructure.web.dto.LocationResponse;
import com.fcv.citas.catalog.infrastructure.web.dto.SpecialtyResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Lectura pública de catálogos (RF-05/RF-06). El USER los consume al buscar disponibilidad. */
@RestController
@RequestMapping("/api/v1")
public class CatalogController {

    private final ListLocationsUseCase listLocations;
    private final ListSpecialtiesUseCase listSpecialties;
    private final ListActiveInsurancePlansUseCase listInsurancePlans;

    public CatalogController(ListLocationsUseCase listLocations, ListSpecialtiesUseCase listSpecialties,
                             ListActiveInsurancePlansUseCase listInsurancePlans) {
        this.listLocations = listLocations;
        this.listSpecialties = listSpecialties;
        this.listInsurancePlans = listInsurancePlans;
    }

    @GetMapping("/catalogs/locations")
    public List<LocationResponse> locations() {
        return listLocations.list().stream().map(LocationResponse::from).toList();
    }

    @GetMapping("/catalogs/plans")
    public List<InsurancePlanResponse> insurancePlans() {
        return listInsurancePlans.listActive().stream().map(InsurancePlanResponse::from).toList();
    }

    /** Solo especialidades activas para el consumo del USER. */
    @GetMapping("/specialties")
    public List<SpecialtyResponse> specialties() {
        return listSpecialties.listActive().stream().map(SpecialtyResponse::from).toList();
    }
}
