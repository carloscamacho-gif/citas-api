package com.fcv.citas.auth.application;

import com.fcv.citas.auth.domain.exception.InvalidRefreshTokenException;
import com.fcv.citas.auth.domain.model.DocumentType;
import com.fcv.citas.auth.domain.model.RefreshToken;
import com.fcv.citas.auth.domain.model.RoleName;
import com.fcv.citas.auth.domain.model.User;
import com.fcv.citas.auth.domain.port.out.RefreshTokenRepositoryPort;
import com.fcv.citas.auth.domain.port.out.TokenIssuerPort;
import com.fcv.citas.auth.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Cubre CA-03 de HU-002 (refresh de sesión). */
class RefreshAccessTokenServiceTest {

    private static final String RAW_TOKEN = "raw-refresh-token";
    private static final String TOKEN_HASH = "hashed-refresh-token";

    private RefreshTokenRepositoryPort refreshTokenRepository;
    private UserRepositoryPort userRepository;
    private TokenIssuerPort tokenIssuer;
    private RefreshAccessTokenService service;

    @BeforeEach
    void setUp() {
        refreshTokenRepository = mock(RefreshTokenRepositoryPort.class);
        userRepository = mock(UserRepositoryPort.class);
        tokenIssuer = mock(TokenIssuerPort.class);
        service = new RefreshAccessTokenService(refreshTokenRepository, userRepository, tokenIssuer);

        when(tokenIssuer.hashRefreshToken(RAW_TOKEN)).thenReturn(TOKEN_HASH);
    }

    private User activeUser(Long id) {
        return new User(id, "Ana", "Pérez", DocumentType.CC, "1000000001", "ana.perez@example.com",
                "3000000000", "hashed-value", Set.of(RoleName.USER), true, Instant.now());
    }

    @Test
    void issuesNewAccessTokenForValidRefreshToken() {
        RefreshToken stored = new RefreshToken(1L, 42L, TOKEN_HASH, Instant.now().plusSeconds(3600), false, Instant.now());
        when(refreshTokenRepository.findValidByTokenHash(TOKEN_HASH)).thenReturn(Optional.of(stored));
        User user = activeUser(42L);
        when(userRepository.findById(42L)).thenReturn(Optional.of(user));
        when(tokenIssuer.issueAccessToken(user)).thenReturn("new-access-token");

        String accessToken = service.refresh(RAW_TOKEN);

        assertThat(accessToken).isEqualTo("new-access-token");
    }

    @Test
    void rejectsUnknownOrExpiredOrRevokedToken() {
        when(refreshTokenRepository.findValidByTokenHash(TOKEN_HASH)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.refresh(RAW_TOKEN))
                .isInstanceOf(InvalidRefreshTokenException.class);

        verify(tokenIssuer, never()).issueAccessToken(any());
    }

    @Test
    void rejectsTokenWhoseUserNoLongerExists() {
        RefreshToken stored = new RefreshToken(1L, 42L, TOKEN_HASH, Instant.now().plusSeconds(3600), false, Instant.now());
        when(refreshTokenRepository.findValidByTokenHash(TOKEN_HASH)).thenReturn(Optional.of(stored));
        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.refresh(RAW_TOKEN))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void rejectsTokenWhoseUserWasDeactivated() {
        RefreshToken stored = new RefreshToken(1L, 42L, TOKEN_HASH, Instant.now().plusSeconds(3600), false, Instant.now());
        when(refreshTokenRepository.findValidByTokenHash(TOKEN_HASH)).thenReturn(Optional.of(stored));
        User inactiveUser = new User(42L, "Ana", "Pérez", DocumentType.CC, "1000000001", "ana.perez@example.com",
                "3000000000", "hashed-value", Set.of(RoleName.USER), false, Instant.now());
        when(userRepository.findById(42L)).thenReturn(Optional.of(inactiveUser));

        assertThatThrownBy(() -> service.refresh(RAW_TOKEN))
                .isInstanceOf(InvalidRefreshTokenException.class);

        verify(tokenIssuer, never()).issueAccessToken(any());
    }
}
