package com.fcv.citas.auth.domain.port.in;

public interface LogoutUseCase {
    void logout(String rawRefreshToken);
}
