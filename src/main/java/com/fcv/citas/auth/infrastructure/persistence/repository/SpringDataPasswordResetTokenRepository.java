package com.fcv.citas.auth.infrastructure.persistence.repository;

import com.fcv.citas.auth.infrastructure.persistence.entity.PasswordResetTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface SpringDataPasswordResetTokenRepository extends JpaRepository<PasswordResetTokenJpaEntity, Long> {

    Optional<PasswordResetTokenJpaEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Query("update PasswordResetTokenJpaEntity t set t.usedAt = :now where t.userId = :userId and t.usedAt is null")
    void markActiveTokensUsed(@Param("userId") Long userId, @Param("now") Instant now);
}
