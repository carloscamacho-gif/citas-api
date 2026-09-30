package com.fcv.citas.auth.domain.port.out;

/** Genera un valor aleatorio opaco y su hash, para tokens de un solo uso (p. ej. recuperación de contraseña). */
public interface OneTimeTokenPort {

    /** Valor aleatorio que se entrega al usuario; del que solo se persiste el hash. */
    String generateRawToken();

    String hash(String rawToken);
}
