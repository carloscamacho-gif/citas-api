package com.fcv.citas.auth.domain.port.in;

import java.util.Optional;

/**
 * HU-003: solicita la recuperación de contraseña de una cuenta por email. Para no revelar si el email existe,
 * siempre se responde igual; el valor devuelto (token en crudo) solo se usa para la exposición controlada en
 * desarrollo y está vacío cuando la cuenta no existe.
 */
public interface RequestPasswordResetUseCase {
    Optional<String> request(String email);
}
