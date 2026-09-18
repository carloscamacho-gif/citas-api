package com.fcv.citas.auth.infrastructure.web;

import com.fcv.citas.auth.domain.model.AuthenticatedSession;
import com.fcv.citas.auth.domain.model.User;
import com.fcv.citas.auth.domain.port.in.LoginUseCase;
import com.fcv.citas.auth.domain.port.in.LogoutUseCase;
import com.fcv.citas.auth.domain.port.in.RefreshAccessTokenUseCase;
import com.fcv.citas.auth.domain.port.in.RegisterUserCommand;
import com.fcv.citas.auth.domain.port.in.RegisterUserUseCase;
import com.fcv.citas.auth.infrastructure.web.dto.LoginRequest;
import com.fcv.citas.auth.infrastructure.web.dto.RefreshRequest;
import com.fcv.citas.auth.infrastructure.web.dto.RegisterRequest;
import com.fcv.citas.auth.infrastructure.web.dto.RegisterResponse;
import com.fcv.citas.auth.infrastructure.web.dto.TokenResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Implementa HU-001 (registro) y HU-002 (login/sesión JWT). Contrato REST — RF-20. */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;
    private final LogoutUseCase logoutUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase, LoginUseCase loginUseCase,
                           RefreshAccessTokenUseCase refreshAccessTokenUseCase, LogoutUseCase logoutUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.refreshAccessTokenUseCase = refreshAccessTokenUseCase;
        this.logoutUseCase = logoutUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = registerUserUseCase.register(new RegisterUserCommand(
                request.firstName(),
                request.lastName(),
                request.documentType(),
                request.documentNumber(),
                request.email(),
                request.phone(),
                request.password()
        ));
        return RegisterResponse.from(user);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        AuthenticatedSession session = loginUseCase.login(request.email(), request.password());
        return TokenResponse.of(session.accessToken(), session.refreshToken());
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        String accessToken = refreshAccessTokenUseCase.refresh(request.refreshToken());
        return TokenResponse.accessOnly(accessToken);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshRequest request) {
        logoutUseCase.logout(request.refreshToken());
    }
}
