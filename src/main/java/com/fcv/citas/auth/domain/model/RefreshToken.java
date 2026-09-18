package com.fcv.citas.auth.domain.model;

import java.time.Instant;

/** Sesión representada por un refresh token; solo se persiste su hash (RF-02). */
public class RefreshToken {

    private final Long id;
    private final Long userId;
    private final String tokenHash;
    private final Instant expiresAt;
    private final boolean revoked;
    private final Instant createdAt;

    public RefreshToken(Long id, Long userId, String tokenHash, Instant expiresAt, boolean revoked, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.createdAt = createdAt;
    }

    public boolean isValid(Instant now) {
        return !revoked && expiresAt.isAfter(now);
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
