package com.fcv.citas.auth.application;

import com.fcv.citas.auth.domain.model.DocumentType;
import com.fcv.citas.auth.domain.model.PasswordResetToken;
import com.fcv.citas.auth.domain.model.RoleName;
import com.fcv.citas.auth.domain.model.User;
import com.fcv.citas.auth.domain.port.out.OneTimeTokenPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** HU-003 / RF-03: solicitud de recuperación con token de un solo uso, sin enumeración de usuarios. */
class RequestPasswordResetServiceTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-09-25T15:00:00Z"), ZoneId.of("America/Bogota"));

    private UserRepositoryPort users;
    private PasswordResetTokenRepositoryPort tokens;
    private OneTimeTokenPort oneTime;

    @BeforeEach
    void setUp() {
        users = mock(UserRepositoryPort.class);
        tokens = mock(PasswordResetTokenRepositoryPort.class);
        oneTime = mock(OneTimeTokenPort.class);
        when(oneTime.generateRawToken()).thenReturn("RAW-TOKEN");
        when(oneTime.hash("RAW-TOKEN")).thenReturn("hash-of-raw");
        when(tokens.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    private RequestPasswordResetService service(boolean expose) {
        return new RequestPasswordResetService(users, tokens, oneTime, CLOCK, 30, expose);
    }

    private User account() {
        return new User(42L, "Ana", "Ruiz", DocumentType.CC, "1", "ana@example.test", "3", "hash",
                Set.of(RoleName.USER), true, Instant.now());
    }

    @Test
    void existingEmailIssuesASingleUseTokenAndInvalidatesPreviousOnes() {
        when(users.findByEmail("ana@example.test")).thenReturn(Optional.of(account()));

        Optional<String> raw = service(true).request("ana@example.test");

        assertThat(raw).contains("RAW-TOKEN");
        verify(tokens).invalidateActiveTokens(42L);
        ArgumentCaptor<PasswordResetToken> saved = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokens).save(saved.capture());
        assertThat(saved.getValue().userId()).isEqualTo(42L);
        assertThat(saved.getValue().tokenHash()).isEqualTo("hash-of-raw");
        assertThat(saved.getValue().usedAt()).isNull();
        assertThat(saved.getValue().expiresAt()).isEqualTo(Instant.parse("2026-09-25T15:30:00Z"));
    }

    @Test
    void unknownEmailDoesNotRevealAnythingNorIssueAToken() {
        when(users.findByEmail("ghost@example.test")).thenReturn(Optional.empty());

        Optional<String> raw = service(true).request("ghost@example.test");

        assertThat(raw).isEmpty();
        verify(tokens, never()).save(any());
        verify(tokens, never()).invalidateActiveTokens(any());
    }

    @Test
    void whenExposureIsOffTheTokenIsIssuedButNotReturned() {
        when(users.findByEmail("ana@example.test")).thenReturn(Optional.of(account()));

        Optional<String> raw = service(false).request("ana@example.test");

        assertThat(raw).isEmpty();
        verify(tokens).save(any()); // el token sí se genera y persiste
    }
}
