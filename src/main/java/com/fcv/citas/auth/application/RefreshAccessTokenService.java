package com.fcv.citas.auth.application;

import com.fcv.citas.auth.domain.exception.InvalidRefreshTokenException;
import com.fcv.citas.auth.domain.model.RefreshToken;
import com.fcv.citas.auth.domain.model.User;
import com.fcv.citas.auth.domain.port.in.RefreshAccessTokenUseCase;
import com.fcv.citas.auth.domain.port.out.RefreshTokenRepositoryPort;
import com.fcv.citas.auth.domain.port.out.TokenIssuerPort;
import com.fcv.citas.auth.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

/** Implementa la parte de refresh de HU-002. */
@Service
public class RefreshAccessTokenService implements RefreshAccessTokenUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final UserRepositoryPort userRepository;
    private final TokenIssuerPort tokenIssuer;

    public RefreshAccessTokenService(RefreshTokenRepositoryPort refreshTokenRepository, UserRepositoryPort userRepository,
                                      TokenIssuerPort tokenIssuer) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.tokenIssuer = tokenIssuer;
    }

    @Override
    public String refresh(String rawRefreshToken) {
        String tokenHash = tokenIssuer.hashRefreshToken(rawRefreshToken);

        RefreshToken stored = refreshTokenRepository.findValidByTokenHash(tokenHash)
                .orElseThrow(InvalidRefreshTokenException::new);

        User user = userRepository.findById(stored.getUserId())
                .filter(User::isActive)
                .orElseThrow(InvalidRefreshTokenException::new);

        return tokenIssuer.issueAccessToken(user);
    }
}
