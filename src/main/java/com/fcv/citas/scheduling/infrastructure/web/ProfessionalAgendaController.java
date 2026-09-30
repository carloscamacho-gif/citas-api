package com.fcv.citas.scheduling.infrastructure.web;

import com.fcv.citas.scheduling.domain.port.in.ProfessionalAgendaUseCase;
import com.fcv.citas.scheduling.infrastructure.web.dto.AppointmentResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/** Agenda del PROFESSIONAL autenticado (HU-020). Rol exigido en SecurityConfig (/professional/**). */
@RestController
@RequestMapping("/api/v1/professional/agenda")
public class ProfessionalAgendaController {

    private final ProfessionalAgendaUseCase agenda;

    public ProfessionalAgendaController(ProfessionalAgendaUseCase agenda) {
        this.agenda = agenda;
    }

    @GetMapping
    public List<AppointmentResponse> agenda(@AuthenticationPrincipal Long userId,
                                            @RequestParam(required = false)
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                            @RequestParam(required = false)
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                            @RequestParam(required = false) Long locationId) {
        return agenda.agenda(userId, from, to, locationId).stream().map(AppointmentResponse::from).toList();
    }
}
