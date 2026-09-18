package com.fcv.citas.auth.domain.port.out;

import com.fcv.citas.auth.domain.model.DocumentType;
import com.fcv.citas.auth.domain.model.User;

import java.util.Optional;

/** Puerto de salida hacia la persistencia de usuarios. La implementación vive en infrastructure/persistence. */
public interface UserRepositoryPort {

    boolean existsByEmail(String email);

    boolean existsByDocument(DocumentType documentType, String documentNumber);

    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);
}
