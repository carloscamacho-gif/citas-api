package com.fcv.citas.auth.domain.port.in;

public interface RefreshAccessTokenUseCase {
    /** Devuelve un nuevo access token si el refresh token presentado es válido. */
    String refresh(String rawRefreshToken);
}
