package com.fcv.citas.scheduling.infrastructure.web;

import com.fcv.citas.scheduling.domain.port.in.AppointmentHistoryUseCase;
import com.fcv.citas.scheduling.domain.port.in.BookAppointmentUseCase;
import com.fcv.citas.scheduling.infrastructure.web.dto.AppointmentResponse;
import com.fcv.citas.scheduling.infrastructure.web.dto.BookAppointmentRequest;
import com.fcv.citas.scheduling.infrastructure.web.dto.StatusHistoryResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Agendamiento del USER (HU-015/HU-016) y consulta de auditoría (HU-024). */
@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    private final BookAppointmentUseCase book;
    private final AppointmentHistoryUseCase history;

    public AppointmentController(BookAppointmentUseCase book, AppointmentHistoryUseCase history) {
        this.book = book;
        this.history = history;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse create(@AuthenticationPrincipal Long userId,
                                      @Valid @RequestBody BookAppointmentRequest request) {
        return AppointmentResponse.from(book.book(userId, request.toCommand()));
    }

    @GetMapping("/{id}/history")
    public List<StatusHistoryResponse> history(@AuthenticationPrincipal Long userId, Authentication authentication,
                                               @PathVariable Long id) {
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replaceFirst("^ROLE_", ""))
                .collect(Collectors.toSet());
        return history.history(userId, roles, id).stream().map(StatusHistoryResponse::from).toList();
    }
}
