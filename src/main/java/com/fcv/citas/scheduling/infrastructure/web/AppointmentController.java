package com.fcv.citas.scheduling.infrastructure.web;

import com.fcv.citas.scheduling.domain.port.in.AppointmentHistoryUseCase;
import com.fcv.citas.scheduling.domain.port.in.BookAppointmentUseCase;
import com.fcv.citas.scheduling.domain.port.in.CancelAppointmentUseCase;
import com.fcv.citas.scheduling.domain.port.in.MyAppointmentsUseCase;
import com.fcv.citas.scheduling.domain.port.in.RequestRescheduleUseCase;
import com.fcv.citas.scheduling.infrastructure.web.dto.AppointmentResponse;
import com.fcv.citas.scheduling.infrastructure.web.dto.BookAppointmentRequest;
import com.fcv.citas.scheduling.infrastructure.web.dto.RescheduleRequestBody;
import com.fcv.citas.scheduling.infrastructure.web.dto.RescheduleResponse;
import com.fcv.citas.scheduling.infrastructure.web.dto.StatusHistoryResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Agendamiento (HU-015/HU-016), "mis citas" (HU-017), cancelación (HU-018) y auditoría (HU-024) del USER. */
@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    private final BookAppointmentUseCase book;
    private final MyAppointmentsUseCase myAppointments;
    private final CancelAppointmentUseCase cancel;
    private final RequestRescheduleUseCase reschedule;
    private final AppointmentHistoryUseCase history;

    public AppointmentController(BookAppointmentUseCase book, MyAppointmentsUseCase myAppointments,
                                 CancelAppointmentUseCase cancel, RequestRescheduleUseCase reschedule,
                                 AppointmentHistoryUseCase history) {
        this.book = book;
        this.myAppointments = myAppointments;
        this.cancel = cancel;
        this.reschedule = reschedule;
        this.history = history;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse create(@AuthenticationPrincipal Long userId,
                                      @Valid @RequestBody BookAppointmentRequest request) {
        return AppointmentResponse.from(book.book(userId, request.toCommand()));
    }

    /** HU-017: mis citas, filtrables por estado y fecha. */
    @GetMapping
    public List<AppointmentResponse> mine(@AuthenticationPrincipal Long userId,
                                          @RequestParam(required = false) String status,
                                          @RequestParam(required = false)
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return myAppointments.list(userId, status, date).stream().map(AppointmentResponse::from).toList();
    }

    /** HU-018: cancelar una cita futura no terminal propia. */
    @PostMapping("/{id}/cancel")
    public AppointmentResponse cancel(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return AppointmentResponse.from(cancel.cancel(userId, id));
    }

    /** HU-019: solicitar reprogramación de una cita aprobada y futura propia. */
    @PostMapping("/{id}/reschedule")
    @ResponseStatus(HttpStatus.CREATED)
    public RescheduleResponse reschedule(@AuthenticationPrincipal Long userId, @PathVariable Long id,
                                         @Valid @RequestBody RescheduleRequestBody request) {
        return RescheduleResponse.from(reschedule.request(userId, request.toCommand(id)));
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
