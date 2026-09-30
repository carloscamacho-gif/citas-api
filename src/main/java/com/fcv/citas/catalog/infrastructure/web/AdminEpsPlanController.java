package com.fcv.citas.catalog.infrastructure.web;

import com.fcv.citas.catalog.domain.port.in.AdminEpsPlanUseCase;
import com.fcv.citas.catalog.domain.port.in.ListInsuranceRegimesUseCase;
import com.fcv.citas.catalog.infrastructure.web.dto.EpsPlanRequest;
import com.fcv.citas.catalog.infrastructure.web.dto.EpsPlanResponse;
import com.fcv.citas.catalog.infrastructure.web.dto.EpsPlanUpdateRequest;
import com.fcv.citas.catalog.infrastructure.web.dto.InsuranceRegimeResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** CRUD de planes de EPS para ADMIN (HU-008). Autorización por rol en SecurityConfig (/admin/**). */
@RestController
@RequestMapping("/api/v1/admin/eps-plans")
public class AdminEpsPlanController {

    private final AdminEpsPlanUseCase plans;
    private final ListInsuranceRegimesUseCase regimes;

    public AdminEpsPlanController(AdminEpsPlanUseCase plans, ListInsuranceRegimesUseCase regimes) {
        this.plans = plans;
        this.regimes = regimes;
    }

    @GetMapping
    public List<EpsPlanResponse> list() {
        return plans.list().stream().map(EpsPlanResponse::from).toList();
    }

    /** Regímenes disponibles para elegir al crear un plan. */
    @GetMapping("/regimes")
    public List<InsuranceRegimeResponse> regimes() {
        return regimes.list().stream().map(InsuranceRegimeResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EpsPlanResponse create(@Valid @RequestBody EpsPlanRequest request) {
        return EpsPlanResponse.from(plans.create(request.toCommand()));
    }

    @PatchMapping("/{id}")
    public EpsPlanResponse update(@PathVariable Long id, @Valid @RequestBody EpsPlanUpdateRequest request) {
        return EpsPlanResponse.from(plans.update(id, request.toCommand()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        plans.delete(id);
    }
}
