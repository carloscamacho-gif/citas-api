package com.fcv.citas.auth.infrastructure.security;

import com.fcv.citas.auth.domain.model.DocumentType;
import com.fcv.citas.auth.domain.model.IssuedRefreshToken;
import com.fcv.citas.auth.domain.model.RoleName;
import com.fcv.citas.auth.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenIssuerAdapterTest {

    private JwtTokenIssuerAdapter adapter;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setAccessSecret("test-access-secret-must-be-32-chars-min");
        properties.setRefreshSecret("test-refresh-secret-must-be-32-chars-min");
        properties.setAccessMinutes(15);
        properties.setRefreshDays(7);
        adapter = new JwtTokenIssuerAdapter(properties);
    }

    private User sampleUser() {
        return new User(42L, "Ana", "Pérez", DocumentType.CC, "1000000001", "ana.perez@example.com",
                "3000000000", "hashed-value", Set.of(RoleName.USER), true, Instant.now());
    }

    @Test
    void issuedAccessTokenRoundTripsToOriginalClaims() {
        User user = sampleUser();

        String token = adapter.issueAccessToken(user);
        AccessTokenClaims claims = adapter.parseAccessToken(token);

        assertThat(claims.userId()).isEqualTo(42L);
        assertThat(claims.email()).isEqualTo("ana.perez@example.com");
        assertThat(claims.roles()).containsExactly("USER");
    }

    @Test
    void refreshTokensAreUniqueAndHashMatchesDeterministically() {
        User user = sampleUser();

        IssuedRefreshToken first = adapter.issueRefreshToken(user);
        IssuedRefreshToken second = adapter.issueRefreshToken(user);

        assertThat(first.rawToken()).isNotEqualTo(second.rawToken());
        assertThat(adapter.hashRefreshToken(first.rawToken())).isEqualTo(first.tokenHash());
        assertThat(adapter.hashRefreshToken(first.rawToken())).isNotEqualTo(second.tokenHash());
    }

    @Test
    void rawRefreshTokenIsNeverStoredAsItsOwnHash() {
        User user = sampleUser();

        IssuedRefreshToken issued = adapter.issueRefreshToken(user);

        assertThat(issued.tokenHash()).isNotEqualTo(issued.rawToken());
    }
}
