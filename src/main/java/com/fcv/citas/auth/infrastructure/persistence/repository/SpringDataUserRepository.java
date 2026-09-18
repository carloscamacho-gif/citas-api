package com.fcv.citas.auth.infrastructure.persistence.repository;

import com.fcv.citas.auth.infrastructure.persistence.entity.DocumentTypeJpa;
import com.fcv.citas.auth.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByDocumentTypeAndDocumentNumber(DocumentTypeJpa documentType, String documentNumber);

    Optional<UserJpaEntity> findByEmailIgnoreCase(String email);
}
