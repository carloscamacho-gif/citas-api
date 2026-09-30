package com.fcv.citas.scheduling.infrastructure.web;

import com.fcv.citas.scheduling.domain.port.in.CloseAttentionUseCase;
import com.fcv.citas.scheduling.infrastructure.web.dto.AppointmentResponse;
import com.fcv.citas.scheduling.infrastructure.web.dto.CloseAttentionRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Cierre de atención del PROFESSIONAL (HU-021). Rol exigido en SecurityConfig (/professional/**). */
@RestController
@RequestMapping("/api/v1/professional/appointments")
public class ProfessionalAttentionController {

    private final CloseAttentionUseCase closeAttention;

    public ProfessionalAttentionController(CloseAttentionUseCase closeAttention) {
        this.closeAttention = closeAttention;
    }

    @PostMapping("/{id}/close")
    public AppointmentResponse close(@AuthenticationPrincipal Long userId, @PathVariable Long id,
                                     @Valid @RequestBody CloseAttentionRequest request) {
        return AppointmentResponse.from(closeAttention.close(userId, id, request.outcome()));
    }
}
