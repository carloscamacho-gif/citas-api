package com.fcv.citas.auth.infrastructure.security;

import com.fcv.citas.auth.domain.model.IssuedRefreshToken;
import com.fcv.citas.auth.domain.model.RoleName;
import com.fcv.citas.auth.domain.model.User;
import com.fcv.citas.auth.domain.port.out.TokenIssuerPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Emite y valida los JWT de acceso, y emite/hashea los refresh tokens opacos (RF-02).
 * El access token es un JWT firmado; el refresh token es un valor aleatorio del que solo se persiste el hash SHA-256.
 */
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class JwtTokenIssuerAdapter implements TokenIssuerPort {

    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLES = "roles";

    private final JwtProperties properties;
    private final SecretKey accessKey;

    public JwtTokenIssuerAdapter(JwtProperties properties) {
        this.properties = properties;
        this.accessKey = Keys.hmacShaKeyFor(properties.getAccessSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String issueAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(properties.getAccessMinutes(), ChronoUnit.MINUTES);

        List<String> roles = user.getRoles().stream().map(RoleName::name).toList();

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim(CLAIM_EMAIL, user.getEmail())
                .claim(CLAIM_ROLES, roles)
                .issuedAt(java.util.Date.from(now))
                .expiration(java.util.Date.from(expiry))
                .signWith(accessKey)
                .compact();
    }

    @Override
    public IssuedRefreshToken issueRefreshToken(User user) {
        String rawToken = generateOpaqueToken();
        Instant expiresAt = Instant.now().plus(properties.getRefreshDays(), ChronoUnit.DAYS);
        return new IssuedRefreshToken(rawToken, hashRefreshToken(rawToken), expiresAt);
    }

    @Override
    public String hashRefreshToken(String rawRefreshToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawRefreshToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible en esta JVM", e);
        }
    }

    /** Usado únicamente por JwtAuthenticationFilter (capa de infraestructura); no forma parte del puerto de dominio. */
    public AccessTokenClaims parseAccessToken(String token) throws JwtException {
        Claims claims = Jwts.parser()
                .verifyWith(accessKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Long userId = Long.valueOf(claims.getSubject());
        String email = claims.get(CLAIM_EMAIL, String.class);
        @SuppressWarnings("unchecked")
        List<String> roles = claims.get(CLAIM_ROLES, List.class);
        Set<String> roleSet = roles == null ? Set.of() : roles.stream().collect(Collectors.toUnmodifiableSet());

        return new AccessTokenClaims(userId, email, roleSet);
    }

    private String generateOpaqueToken() {
        byte[] randomBytes = new byte[32];
        new java.security.SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
