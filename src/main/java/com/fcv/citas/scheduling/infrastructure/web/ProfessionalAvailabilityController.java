package com.fcv.citas.scheduling.infrastructure.web;

import com.fcv.citas.scheduling.domain.port.in.AvailabilityBlockUseCase;
import com.fcv.citas.scheduling.infrastructure.web.dto.AvailabilityBlockRequest;
import com.fcv.citas.scheduling.infrastructure.web.dto.AvailabilityBlockResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/** Bloques de disponibilidad del PROFESSIONAL autenticado (HU-012). Rol exigido en SecurityConfig. */
@RestController
@RequestMapping("/api/v1/professional/availability-blocks")
public class ProfessionalAvailabilityController {

    private final AvailabilityBlockUseCase blocks;

    public ProfessionalAvailabilityController(AvailabilityBlockUseCase blocks) {
        this.blocks = blocks;
    }

    @GetMapping
    public List<AvailabilityBlockResponse> list(@AuthenticationPrincipal Long userId,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                @RequestParam(required = false) Long locationId) {
        return blocks.list(userId, date, locationId).stream().map(AvailabilityBlockResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AvailabilityBlockResponse create(@AuthenticationPrincipal Long userId,
                                            @Valid @RequestBody AvailabilityBlockRequest request) {
        return AvailabilityBlockResponse.from(blocks.create(userId, request.toCommand()));
    }

    @PatchMapping("/{id}")
    public AvailabilityBlockResponse update(@AuthenticationPrincipal Long userId, @PathVariable Long id,
                                            @Valid @RequestBody AvailabilityBlockRequest request) {
        return AvailabilityBlockResponse.from(blocks.update(userId, id, request.toCommand()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        blocks.delete(userId, id);
    }
}
