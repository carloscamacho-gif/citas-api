package com.fcv.citas.auth.domain.model;

import java.time.Instant;

/**
 * Token de recuperación de contraseña (HU-003). Solo se persiste el hash del valor aleatorio; es de un
 * solo uso (se marca {@code usedAt} al consumirse) y expira en {@code expiresAt} (RF-03).
 */
public record PasswordResetToken(Long id, Long userId, String tokenHash, Instant expiresAt, Instant usedAt,
                                 Instant createdAt) {

    public boolean isUsable(Instant now) {
        return usedAt == null && expiresAt.isAfter(now);
    }

    public PasswordResetToken consumed(Instant at) {
        return new PasswordResetToken(id, userId, tokenHash, expiresAt, at, createdAt);
    }
}
