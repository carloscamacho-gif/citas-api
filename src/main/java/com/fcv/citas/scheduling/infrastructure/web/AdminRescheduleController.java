package com.fcv.citas.scheduling.infrastructure.web;

import com.fcv.citas.scheduling.domain.port.in.RescheduleDecisionUseCase;
import com.fcv.citas.scheduling.domain.port.in.RescheduleInboxFilter;
import com.fcv.citas.scheduling.domain.port.in.RescheduleInboxUseCase;
import com.fcv.citas.scheduling.infrastructure.web.dto.DecisionRequest;
import com.fcv.citas.scheduling.infrastructure.web.dto.RescheduleResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/** Bandeja y decisión de reprogramaciones para ADMIN (HU-023). Rol exigido en SecurityConfig (/admin/**). */
@RestController
@RequestMapping("/api/v1/admin/reschedules")
public class AdminRescheduleController {

    private final RescheduleInboxUseCase inbox;
    private final RescheduleDecisionUseCase decisions;

    public AdminRescheduleController(RescheduleInboxUseCase inbox, RescheduleDecisionUseCase decisions) {
        this.inbox = inbox;
        this.decisions = decisions;
    }

    @GetMapping("/pending")
    public List<RescheduleResponse> pending(
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) Long professionalId,
            @RequestParam(required = false) Long specialtyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return inbox.pending(new RescheduleInboxFilter(locationId, professionalId, specialtyId, date))
                .stream().map(RescheduleResponse::from).toList();
    }

    @PostMapping("/{id}/decision")
    public RescheduleResponse decide(@AuthenticationPrincipal Long adminUserId, @PathVariable Long id,
                                     @Valid @RequestBody DecisionRequest request) {
        return RescheduleResponse.from(decisions.decide(adminUserId, id, request.decision(), request.reason()));
    }
}
