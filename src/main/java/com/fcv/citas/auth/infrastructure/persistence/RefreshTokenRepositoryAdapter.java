package com.fcv.citas.auth.infrastructure.persistence;

import com.fcv.citas.auth.domain.model.RefreshToken;
import com.fcv.citas.auth.domain.port.out.RefreshTokenRepositoryPort;
import com.fcv.citas.auth.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import com.fcv.citas.auth.infrastructure.persistence.repository.SpringDataRefreshTokenRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final SpringDataRefreshTokenRepository refreshTokenRepository;

    public RefreshTokenRepositoryAdapter(SpringDataRefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity entity = new RefreshTokenJpaEntity(
                refreshToken.getUserId(),
                refreshToken.getTokenHash(),
                refreshToken.getExpiresAt(),
                refreshToken.isRevoked()
        );
        RefreshTokenJpaEntity saved = refreshTokenRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findValidByTokenHash(String tokenHash) {
        return refreshTokenRepository.findByTokenHash(tokenHash)
                .map(RefreshTokenRepositoryAdapter::toDomain)
                .filter(token -> token.isValid(Instant.now()));
    }

    @Override
    public void revoke(String tokenHash) {
        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(entity -> {
            entity.setRevoked(true);
            refreshTokenRepository.save(entity);
        });
    }

    private static RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        return new RefreshToken(
                entity.getId(),
                entity.getUserId(),
                entity.getTokenHash(),
                entity.getExpiresAt(),
                entity.isRevoked(),
                entity.getCreatedAt()
        );
    }
}
