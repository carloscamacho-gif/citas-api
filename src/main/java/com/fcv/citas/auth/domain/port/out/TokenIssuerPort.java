package com.fcv.citas.auth.domain.port.out;

import com.fcv.citas.auth.domain.model.IssuedRefreshToken;
import com.fcv.citas.auth.domain.model.User;

/** Aísla al dominio del formato concreto de los tokens (JWT) usado en infrastructure/security. */
public interface TokenIssuerPort {

    String issueAccessToken(User user);

    IssuedRefreshToken issueRefreshToken(User user);

    /** Usado para localizar en el repositorio el refresh token que el cliente presenta en claro. */
    String hashRefreshToken(String rawRefreshToken);
}
