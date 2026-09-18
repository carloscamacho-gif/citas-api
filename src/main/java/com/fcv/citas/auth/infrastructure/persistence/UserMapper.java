package com.fcv.citas.auth.infrastructure.persistence;

import com.fcv.citas.auth.domain.model.DocumentType;
import com.fcv.citas.auth.domain.model.RoleName;
import com.fcv.citas.auth.domain.model.User;
import com.fcv.citas.auth.infrastructure.persistence.entity.DocumentTypeJpa;
import com.fcv.citas.auth.infrastructure.persistence.entity.RoleJpaEntity;
import com.fcv.citas.auth.infrastructure.persistence.entity.UserJpaEntity;

import java.util.Set;
import java.util.stream.Collectors;

/** Traduce entre el modelo de dominio (agnóstico de JPA) y la entidad de persistencia. */
final class UserMapper {

    private UserMapper() {
    }

    static UserJpaEntity toNewEntity(User user, Set<RoleJpaEntity> roleEntities) {
        return new UserJpaEntity(
                user.getFirstName(),
                user.getLastName(),
                DocumentTypeJpa.valueOf(user.getDocumentType().name()),
                user.getDocumentNumber(),
                user.getEmail(),
                user.getPhone(),
                user.getPasswordHash(),
                user.isActive(),
                roleEntities
        );
    }

    static User toDomain(UserJpaEntity entity) {
        Set<RoleName> roles = entity.getRoles().stream()
                .map(RoleJpaEntity::getName)
                .map(RoleName::valueOf)
                .collect(Collectors.toSet());

        User user = new User(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                DocumentType.valueOf(entity.getDocumentType().name()),
                entity.getDocumentNumber(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getPasswordHash(),
                roles,
                entity.isActive(),
                entity.getCreatedAt()
        );
        return user;
    }
}
