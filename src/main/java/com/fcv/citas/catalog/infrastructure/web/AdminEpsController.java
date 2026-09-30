package com.fcv.citas.catalog.infrastructure.web;

import com.fcv.citas.catalog.domain.port.in.AdminEpsUseCase;
import com.fcv.citas.catalog.infrastructure.web.dto.EpsRequest;
import com.fcv.citas.catalog.infrastructure.web.dto.EpsResponse;
import com.fcv.citas.catalog.infrastructure.web.dto.EpsUpdateRequest;
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

/** CRUD de EPS para ADMIN (HU-007). Autorización por rol en SecurityConfig (/admin/**). */
@RestController
@RequestMapping("/api/v1/admin/eps")
public class AdminEpsController {

    private final AdminEpsUseCase eps;

    public AdminEpsController(AdminEpsUseCase eps) {
        this.eps = eps;
    }

    @GetMapping
    public List<EpsResponse> list() {
        return eps.list().stream().map(EpsResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EpsResponse create(@Valid @RequestBody EpsRequest request) {
        return EpsResponse.from(eps.create(request.toCommand()));
    }

    @PatchMapping("/{id}")
    public EpsResponse update(@PathVariable Long id, @Valid @RequestBody EpsUpdateRequest request) {
        return EpsResponse.from(eps.update(id, request.toCommand()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        eps.delete(id);
    }
}
