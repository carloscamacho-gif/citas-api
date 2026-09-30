package com.fcv.citas.auth.domain.port.out;

import com.fcv.citas.auth.domain.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepositoryPort {

    RefreshToken save(RefreshToken refreshToken);

    /** Solo devuelve el token si existe, no está revocado y no expiró. */
    Optional<RefreshToken> findValidByTokenHash(String tokenHash);

    /** Idempotente: si el hash no existe, no hace nada (evita filtrar información en logout). */
    void revoke(String tokenHash);
}
