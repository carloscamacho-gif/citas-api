package com.fcv.citas.auth.application;

import com.fcv.citas.auth.domain.exception.InvalidCredentialsException;
import com.fcv.citas.auth.domain.model.AuthenticatedSession;
import com.fcv.citas.auth.domain.model.DocumentType;
import com.fcv.citas.auth.domain.model.IssuedRefreshToken;
import com.fcv.citas.auth.domain.model.RoleName;
import com.fcv.citas.auth.domain.model.User;
import com.fcv.citas.auth.domain.port.out.PasswordHasherPort;
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

/** Cubre CA-01 a CA-04 de HU-002 (Iniciar sesión) — parte de login. */
class LoginServiceTest {

    private static final String EMAIL = "ana.perez@example.com";
    private static final String RAW_PASSWORD = "S3cret123!";

    private UserRepositoryPort userRepository;
    private PasswordHasherPort passwordHasher;
    private TokenIssuerPort tokenIssuer;
    private RefreshTokenRepositoryPort refreshTokenRepository;
    private LoginService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepositoryPort.class);
        passwordHasher = mock(PasswordHasherPort.class);
        tokenIssuer = mock(TokenIssuerPort.class);
        refreshTokenRepository = mock(RefreshTokenRepositoryPort.class);
        service = new LoginService(userRepository, passwordHasher, tokenIssuer, refreshTokenRepository);
    }

    private User activeUser() {
        return new User(1L, "Ana", "Pérez", DocumentType.CC, "1000000001", EMAIL, "3000000000",
                "hashed-value", Set.of(RoleName.USER), true, Instant.now());
    }

    @Test
    void issuesTokensOnValidCredentials() {
        User user = activeUser();
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordHasher.matches(RAW_PASSWORD, user.getPasswordHash())).thenReturn(true);
        when(tokenIssuer.issueAccessToken(user)).thenReturn("access-token");
        when(tokenIssuer.issueRefreshToken(user)).thenReturn(
                new IssuedRefreshToken("raw-refresh", "hashed-refresh", Instant.now().plusSeconds(3600)));

        AuthenticatedSession session = service.login(EMAIL, RAW_PASSWORD);

        assertThat(session.accessToken()).isEqualTo("access-token");
        assertThat(session.refreshToken()).isEqualTo("raw-refresh");
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void rejectsUnknownEmailWithGenericError() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(EMAIL, RAW_PASSWORD))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(tokenIssuer, never()).issueAccessToken(any());
    }

    @Test
    void rejectsWrongPasswordWithGenericError() {
        User user = activeUser();
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordHasher.matches(RAW_PASSWORD, user.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> service.login(EMAIL, RAW_PASSWORD))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(tokenIssuer, never()).issueAccessToken(any());
    }

    @Test
    void rejectsInactiveUser() {
        User inactiveUser = new User(1L, "Ana", "Pérez", DocumentType.CC, "1000000001", EMAIL, "3000000000",
                "hashed-value", Set.of(RoleName.USER), false, Instant.now());
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(inactiveUser));

        assertThatThrownBy(() -> service.login(EMAIL, RAW_PASSWORD))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
