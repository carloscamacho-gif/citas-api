package com.fcv.citas.auth.domain.model;

/** Resultado de un login o refresh exitoso, listo para exponerse al cliente. */
public record AuthenticatedSession(String accessToken, String refreshToken) {
}
