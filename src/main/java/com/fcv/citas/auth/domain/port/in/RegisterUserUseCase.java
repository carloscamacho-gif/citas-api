package com.fcv.citas.auth.domain.port.in;

import com.fcv.citas.auth.domain.model.User;

public interface RegisterUserUseCase {
    User register(RegisterUserCommand command);
}
