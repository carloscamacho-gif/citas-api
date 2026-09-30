package com.fcv.citas.auth.infrastructure.persistence;

import com.fcv.citas.auth.domain.model.PasswordResetToken;
import com.fcv.citas.auth.domain.port.out.PasswordResetTokenRepositoryPort;
import com.fcv.citas.auth.infrastructure.persistence.entity.PasswordResetTokenJpaEntity;
import com.fcv.citas.auth.infrastructure.persistence.repository.SpringDataPasswordResetTokenRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class PasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepositoryPort {

    private final SpringDataPasswordResetTokenRepository repository;

    public PasswordResetTokenRepositoryAdapter(SpringDataPasswordResetTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        PasswordResetTokenJpaEntity saved = repository.save(new PasswordResetTokenJpaEntity(
                token.id(), token.userId(), token.tokenHash(), token.expiresAt(), token.usedAt()));
        return toDomain(saved);
    }

    @Override
    public Optional<PasswordResetToken> findByTokenHash(String tokenHash) {
        return repository.findByTokenHash(tokenHash).map(PasswordResetTokenRepositoryAdapter::toDomain);
    }

    @Override
    public void invalidateActiveTokens(Long userId) {
        repository.markActiveTokensUsed(userId, Instant.now());
    }

    private static PasswordResetToken toDomain(PasswordResetTokenJpaEntity e) {
        return new PasswordResetToken(e.getId(), e.getUserId(), e.getTokenHash(), e.getExpiresAt(),
                e.getUsedAt(), e.getCreatedAt());
    }
}
