package com.fcv.citas.auth.domain.port.out;

/** Aísla al dominio del algoritmo de hash concreto (BCrypt/Argon2) usado en infrastructure/security. */
public interface PasswordHasherPort {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String passwordHash);
}
