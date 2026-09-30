package com.fcv.citas.auth.infrastructure.web.dto;

import com.fcv.citas.auth.domain.model.User;

import java.util.Set;
import java.util.stream.Collectors;

/** Respuesta de registro. Nunca incluye la contraseña ni su hash (RF-01, CA-01). */
public record RegisterResponse(Long id, String firstName, String lastName, String email, Set<String> roles) {

    public static RegisterResponse from(User user) {
        return new RegisterResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet())
        );
    }
}
