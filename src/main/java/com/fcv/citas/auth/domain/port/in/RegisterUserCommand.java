package com.fcv.citas.auth.domain.port.in;

import com.fcv.citas.auth.domain.model.DocumentType;

public record RegisterUserCommand(
        String firstName,
        String lastName,
        DocumentType documentType,
        String documentNumber,
        String email,
        String phone,
        String rawPassword
) {
}
