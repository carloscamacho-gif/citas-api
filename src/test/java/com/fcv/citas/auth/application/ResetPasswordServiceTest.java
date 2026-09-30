package com.fcv.citas.auth.application;

import com.fcv.citas.auth.domain.exception.InvalidPasswordResetTokenException;
import com.fcv.citas.auth.domain.model.DocumentType;
import com.fcv.citas.auth.domain.model.PasswordResetToken;
import com.fcv.citas.auth.domain.model.RoleName;
import com.fcv.citas.auth.domain.model.User;
import com.fcv.citas.auth.domain.port.out.OneTimeTokenPort;
import com.fcv.citas.auth.domain.port.out.PasswordHasherPort;
import com.fcv.citas.auth.domain.port.out.PasswordResetTokenRepositoryPort;
import com.fcv.citas.auth.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** HU-003 / RF-03: cambio de contraseña con token, consumo del token y rechazo de tokens usados/expirados. */
class ResetPasswordServiceTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-09-25T15:00:00Z"), ZoneId.of("America/Bogota"));
    private static final Instant NOW = Instant.parse("2026-09-25T15:00:00Z");

    private PasswordResetTokenRepositoryPort tokens;
    private UserRepositoryPort users;
    private PasswordHasherPort passwordHasher;
    private OneTimeTokenPort oneTime;
    private ResetPasswordService service;

    @BeforeEach
    void setUp() {
        tokens = mock(PasswordResetTokenRepositoryPort.class);
        users = mock(UserRepositoryPort.class);
        passwordHasher = mock(PasswordHasherPort.class);
        oneTime = mock(OneTimeTokenPort.class);
        service = new ResetPasswordService(tokens, users, passwordHasher, oneTime, CLOCK);
        when(oneTime.hash("RAW")).thenReturn("hash-of-raw");
        when(passwordHasher.hash("NuevaClave1")).thenReturn("nuevo-hash");
        when(users.findById(42L)).thenReturn(Optional.of(new User(42L, "Ana", "Ruiz", DocumentType.CC, "1",
                "ana@example.test", "3", "viejo-hash", Set.of(RoleName.USER), true, Instant.now())));
    }

    private PasswordResetToken token(Instant expiresAt, Instant usedAt) {
        return new PasswordResetToken(9L, 42L, "hash-of-raw", expiresAt, usedAt, NOW.minusSeconds(60));
    }

    @Test
    void validTokenUpdatesThePasswordAndConsumesTheToken() {
        when(tokens.findByTokenHash("hash-of-raw")).thenReturn(Optional.of(token(NOW.plusSeconds(600), null)));

        service.reset("RAW", "NuevaClave1");

        verify(users).updatePassword(42L, "nuevo-hash");
        ArgumentCaptor<PasswordResetToken> consumed = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokens).save(consumed.capture());
        assertThat(consumed.getValue().usedAt()).isEqualTo(NOW);
    }

    @Test
    void anExpiredTokenIsRejectedWithoutChangingThePassword() {
        when(tokens.findByTokenHash("hash-of-raw")).thenReturn(Optional.of(token(NOW.minusSeconds(1), null)));

        assertThatThrownBy(() -> service.reset("RAW", "NuevaClave1"))
                .isInstanceOf(InvalidPasswordResetTokenException.class);
        verify(users, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    void anAlreadyUsedTokenIsRejected() {
        when(tokens.findByTokenHash("hash-of-raw")).thenReturn(Optional.of(token(NOW.plusSeconds(600), NOW.minusSeconds(30))));

        assertThatThrownBy(() -> service.reset("RAW", "NuevaClave1"))
                .isInstanceOf(InvalidPasswordResetTokenException.class);
        verify(users, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    void anUnknownTokenIsRejected() {
        when(tokens.findByTokenHash("hash-of-raw")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.reset("RAW", "NuevaClave1"))
                .isInstanceOf(InvalidPasswordResetTokenException.class);
    }

    @Test
    void aTooShortPasswordIsRejectedBeforeTouchingTheToken() {
        assertThatThrownBy(() -> service.reset("RAW", "corta"))
                .isInstanceOf(IllegalArgumentException.class);
        verify(tokens, never()).findByTokenHash(eq("hash-of-raw"));
        verify(users, never()).updatePassword(anyLong(), anyString());
    }
}
