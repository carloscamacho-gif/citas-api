package com.fcv.citas.auth.application;

import com.fcv.citas.auth.domain.exception.InvalidPasswordResetTokenException;
import com.fcv.citas.auth.domain.model.PasswordResetToken;
import com.fcv.citas.auth.domain.model.User;
import com.fcv.citas.auth.domain.port.in.ResetPasswordUseCase;
import com.fcv.citas.auth.domain.port.out.OneTimeTokenPort;
import com.fcv.citas.auth.domain.port.out.PasswordHasherPort;
import com.fcv.citas.auth.domain.port.out.PasswordResetTokenRepositoryPort;
import com.fcv.citas.auth.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

/**
 * Implementa HU-003 (cambio de contraseña con token). Valida que el token esté vigente y no usado, actualiza la
 * contraseña con hash adaptativo y consume el token (RF-03). La contraseña nueva nunca se registra en texto plano.
 */
@Service
public class ResetPasswordService implements ResetPasswordUseCase {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final PasswordResetTokenRepositoryPort tokens;
    private final UserRepositoryPort users;
    private final PasswordHasherPort passwordHasher;
    private final OneTimeTokenPort oneTimeTokens;
    private final Clock clock;

    public ResetPasswordService(PasswordResetTokenRepositoryPort tokens, UserRepositoryPort users,
                                PasswordHasherPort passwordHasher, OneTimeTokenPort oneTimeTokens, Clock clock) {
        this.tokens = tokens;
        this.users = users;
        this.passwordHasher = passwordHasher;
        this.oneTimeTokens = oneTimeTokens;
        this.clock = clock;
    }

    @Override
    @Transactional
    public void reset(String rawToken, String newPassword) {
        if (newPassword == null || newPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres");
        }
        if (rawToken == null || rawToken.isBlank()) {
            throw new InvalidPasswordResetTokenException();
        }
        Instant now = Instant.now(clock);
        PasswordResetToken token = tokens.findByTokenHash(oneTimeTokens.hash(rawToken))
                .filter(t -> t.isUsable(now))
                .orElseThrow(InvalidPasswordResetTokenException::new);

        User user = users.findById(token.userId()).orElseThrow(InvalidPasswordResetTokenException::new);
        users.updatePassword(user.getId(), passwordHasher.hash(newPassword));
        // Consumo del token: de un solo uso (RF-03).
        tokens.save(token.consumed(now));
    }
}
