package com.fcv.citas.scheduling.infrastructure.web;

import com.fcv.citas.scheduling.domain.port.in.AppointmentDecisionUseCase;
import com.fcv.citas.scheduling.domain.port.in.PendingAppointmentFilter;
import com.fcv.citas.scheduling.domain.port.in.UpcomingAppointmentsUseCase;
import com.fcv.citas.scheduling.infrastructure.web.dto.AppointmentResponse;
import com.fcv.citas.scheduling.infrastructure.web.dto.DecisionRequest;
import com.fcv.citas.scheduling.infrastructure.web.dto.UpcomingAppointmentResponse;
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

/** Bandeja y decisión de citas especializadas para ADMIN (HU-022). Rol exigido en SecurityConfig (/admin/**). */
@RestController
@RequestMapping("/api/v1/admin/appointments")
public class AdminAppointmentController {

    private final AppointmentDecisionUseCase decisions;
    private final UpcomingAppointmentsUseCase upcoming;

    public AdminAppointmentController(AppointmentDecisionUseCase decisions, UpcomingAppointmentsUseCase upcoming) {
        this.decisions = decisions;
        this.upcoming = upcoming;
    }

    @GetMapping("/pending-specialized")
    public List<AppointmentResponse> pending(
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) Long professionalId,
            @RequestParam(required = false) Long specialtyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return decisions.pendingSpecialized(new PendingAppointmentFilter(locationId, professionalId, specialtyId, date))
                .stream().map(AppointmentResponse::from).toList();
    }

    /** S5 (WF-001/WF-003): citas APPROVED en un rango de días (por defecto hoy→mañana) para las automatizaciones. */
    @GetMapping("/upcoming")
    public List<UpcomingAppointmentResponse> upcoming(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long locationId) {
        return upcoming.upcoming(from, to, locationId).stream().map(UpcomingAppointmentResponse::from).toList();
    }

    @PostMapping("/{id}/decision")
    public AppointmentResponse decide(@AuthenticationPrincipal Long adminUserId, @PathVariable Long id,
                                      @Valid @RequestBody DecisionRequest request) {
        return AppointmentResponse.from(decisions.decide(adminUserId, id, request.decision(), request.reason()));
    }
}
