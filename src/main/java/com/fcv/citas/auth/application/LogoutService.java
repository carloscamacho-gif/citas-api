package com.fcv.citas.auth.application;

import com.fcv.citas.auth.domain.port.in.LogoutUseCase;
import com.fcv.citas.auth.domain.port.out.RefreshTokenRepositoryPort;
import com.fcv.citas.auth.domain.port.out.TokenIssuerPort;
import org.springframework.stereotype.Service;

/** Implementa la parte de logout/revocación de HU-002. */
@Service
public class LogoutService implements LogoutUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final TokenIssuerPort tokenIssuer;

    public LogoutService(RefreshTokenRepositoryPort refreshTokenRepository, TokenIssuerPort tokenIssuer) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenIssuer = tokenIssuer;
    }

    @Override
    public void logout(String rawRefreshToken) {
        String tokenHash = tokenIssuer.hashRefreshToken(rawRefreshToken);
        refreshTokenRepository.revoke(tokenHash);
    }
}
