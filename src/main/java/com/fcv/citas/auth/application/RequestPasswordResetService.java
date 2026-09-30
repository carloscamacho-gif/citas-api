package com.fcv.citas.auth.application;

import com.fcv.citas.auth.domain.model.PasswordResetToken;
import com.fcv.citas.auth.domain.model.User;
import com.fcv.citas.auth.domain.port.in.RequestPasswordResetUseCase;
import com.fcv.citas.auth.domain.port.out.OneTimeTokenPort;
import com.fcv.citas.auth.domain.port.out.PasswordResetTokenRepositoryPort;
import com.fcv.citas.auth.domain.port.out.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Implementa HU-003 (solicitud de recuperación). Para no revelar si un email está registrado, la respuesta es
 * siempre la misma; solo si la cuenta existe se genera un token de un solo uso. En el laboratorio, el token se
 * expone de forma controlada (log/respuesta) para completar el flujo sin SMTP (RF-03). Nunca se registra la contraseña.
 */
@Service
public class RequestPasswordResetService implements RequestPasswordResetUseCase {

    private static final Logger log = LoggerFactory.getLogger(RequestPasswordResetService.class);

    private final UserRepositoryPort users;
    private final PasswordResetTokenRepositoryPort tokens;
    private final OneTimeTokenPort oneTimeTokens;
    private final Clock clock;
    private final long ttlMinutes;
    private final boolean exposeToken;

    public RequestPasswordResetService(UserRepositoryPort users, PasswordResetTokenRepositoryPort tokens,
                                       OneTimeTokenPort oneTimeTokens, Clock clock,
                                       @Value("${app.password-reset.minutes:30}") long ttlMinutes,
                                       @Value("${app.password-reset.expose-token:true}") boolean exposeToken) {
        this.users = users;
        this.tokens = tokens;
        this.oneTimeTokens = oneTimeTokens;
        this.clock = clock;
        this.ttlMinutes = ttlMinutes;
        this.exposeToken = exposeToken;
    }

    @Override
    @Transactional
    public Optional<String> request(String email) {
        Optional<User> account = users.findByEmail(email);
        if (account.isEmpty()) {
            // Sin enumeración de usuarios: se responde igual que si existiera, pero no se emite token.
            log.info("Solicitud de recuperación para un email sin cuenta asociada; no se emite token.");
            return Optional.empty();
        }
        User user = account.get();
        // Una nueva solicitud invalida las anteriores del mismo usuario (un solo token vigente).
        tokens.invalidateActiveTokens(user.getId());

        String rawToken = oneTimeTokens.generateRawToken();
        Instant expiresAt = Instant.now(clock).plus(ttlMinutes, ChronoUnit.MINUTES);
        tokens.save(new PasswordResetToken(null, user.getId(), oneTimeTokens.hash(rawToken), expiresAt, null, null));

        if (exposeToken) {
            // Exposición controlada de laboratorio (sin SMTP). Se registra el token, nunca la contraseña.
            log.info("Token de recuperación (laboratorio) para userId={}: {}", user.getId(), rawToken);
            return Optional.of(rawToken);
        }
        log.info("Token de recuperación emitido para userId={} (no expuesto).", user.getId());
        return Optional.empty();
    }
}
