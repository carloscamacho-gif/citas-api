package com.fcv.citas.auth.domain.port.in;

import com.fcv.citas.auth.domain.model.AuthenticatedSession;

public interface LoginUseCase {
    AuthenticatedSession login(String email, String rawPassword);
}
