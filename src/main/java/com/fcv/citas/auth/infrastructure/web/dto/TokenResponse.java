package com.fcv.citas.auth.infrastructure.web.dto;

/** Respuesta de login/refresh. `refreshToken` viene null en la respuesta de /refresh (no se reemite). */
public record TokenResponse(String accessToken, String refreshToken, String tokenType) {

    public static TokenResponse of(String accessToken, String refreshToken) {
        return new TokenResponse(accessToken, refreshToken, "Bearer");
    }

    public static TokenResponse accessOnly(String accessToken) {
        return new TokenResponse(accessToken, null, "Bearer");
    }
}
