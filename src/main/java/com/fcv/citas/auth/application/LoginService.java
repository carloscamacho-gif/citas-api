package com.fcv.citas.auth.application;

import com.fcv.citas.auth.domain.exception.InvalidCredentialsException;
import com.fcv.citas.auth.domain.model.AuthenticatedSession;
import com.fcv.citas.auth.domain.model.IssuedRefreshToken;
import com.fcv.citas.auth.domain.model.RefreshToken;
import com.fcv.citas.auth.domain.model.User;
import com.fcv.citas.auth.domain.port.in.LoginUseCase;
import com.fcv.citas.auth.domain.port.out.PasswordHasherPort;
import com.fcv.citas.auth.domain.port.out.RefreshTokenRepositoryPort;
import com.fcv.citas.auth.domain.port.out.TokenIssuerPort;
import com.fcv.citas.auth.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

/** Implementa HU-002 (Iniciar sesión y sesión JWT) — parte de login. */
@Service
public class LoginService implements LoginUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final TokenIssuerPort tokenIssuer;
    private final RefreshTokenRepositoryPort refreshTokenRepository;

    public LoginService(UserRepositoryPort userRepository, PasswordHasherPort passwordHasher,
                         TokenIssuerPort tokenIssuer, RefreshTokenRepositoryPort refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenIssuer = tokenIssuer;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public AuthenticatedSession login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive() || !passwordHasher.matches(rawPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = tokenIssuer.issueAccessToken(user);
        IssuedRefreshToken issued = tokenIssuer.issueRefreshToken(user);

        refreshTokenRepository.save(new RefreshToken(null, user.getId(), issued.tokenHash(), issued.expiresAt(), false, null));

        return new AuthenticatedSession(accessToken, issued.rawToken());
    }
}
