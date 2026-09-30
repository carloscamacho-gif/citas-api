package com.fcv.citas.auth.domain.port.out;

import com.fcv.citas.auth.domain.model.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepositoryPort {

    PasswordResetToken save(PasswordResetToken token);

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    /** Marca como usados todos los tokens vigentes de un usuario (una nueva solicitud invalida las anteriores). */
    void invalidateActiveTokens(Long userId);
}
