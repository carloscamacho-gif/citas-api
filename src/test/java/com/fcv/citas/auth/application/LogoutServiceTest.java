package com.fcv.citas.auth.application;

import com.fcv.citas.auth.domain.port.out.RefreshTokenRepositoryPort;
import com.fcv.citas.auth.domain.port.out.TokenIssuerPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Cubre CA-04 de HU-002 (logout / revocación). */
class LogoutServiceTest {

    private static final String RAW_TOKEN = "raw-refresh-token";
    private static final String TOKEN_HASH = "hashed-refresh-token";

    private RefreshTokenRepositoryPort refreshTokenRepository;
    private TokenIssuerPort tokenIssuer;
    private LogoutService service;

    @BeforeEach
    void setUp() {
        refreshTokenRepository = mock(RefreshTokenRepositoryPort.class);
        tokenIssuer = mock(TokenIssuerPort.class);
        service = new LogoutService(refreshTokenRepository, tokenIssuer);

        when(tokenIssuer.hashRefreshToken(RAW_TOKEN)).thenReturn(TOKEN_HASH);
    }

    @Test
    void revokesTheHashedRefreshToken() {
        service.logout(RAW_TOKEN);

        verify(refreshTokenRepository).revoke(TOKEN_HASH);
    }

    @Test
    void isIdempotentForATokenThatDoesNotExist() {
        // revoke() en el puerto es idempotente por contrato: no lanza aunque el hash no exista.
        assertThatCode(() -> service.logout(RAW_TOKEN)).doesNotThrowAnyException();
    }
}
