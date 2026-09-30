package com.fcv.citas.auth.domain.model;

import java.time.Instant;

/** Resultado de emitir un refresh token: el valor crudo (para el cliente) y su hash (para persistir). */
public record IssuedRefreshToken(String rawToken, String tokenHash, Instant expiresAt) {
}
