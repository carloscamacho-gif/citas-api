package com.fcv.citas.auth.infrastructure.security;

import java.util.Set;

/** Resultado de validar/parsear un access token; usado por JwtAuthenticationFilter para poblar el contexto de autorización. */
public record AccessTokenClaims(Long userId, String email, Set<String> roles) {
}
