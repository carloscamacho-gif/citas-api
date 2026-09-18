package com.fcv.citas.auth.infrastructure.persistence;

import com.fcv.citas.auth.domain.model.DocumentType;
import com.fcv.citas.auth.domain.model.RoleName;
import com.fcv.citas.auth.domain.model.User;
import com.fcv.citas.auth.domain.port.out.UserRepositoryPort;
import com.fcv.citas.auth.infrastructure.persistence.entity.DocumentTypeJpa;
import com.fcv.citas.auth.infrastructure.persistence.entity.RoleJpaEntity;
import com.fcv.citas.auth.infrastructure.persistence.entity.UserJpaEntity;
import com.fcv.citas.auth.infrastructure.persistence.repository.SpringDataRoleRepository;
import com.fcv.citas.auth.infrastructure.persistence.repository.SpringDataUserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository userRepository;
    private final SpringDataRoleRepository roleRepository;

    public UserRepositoryAdapter(SpringDataUserRepository userRepository, SpringDataRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean existsByDocument(DocumentType documentType, String documentNumber) {
        return userRepository.existsByDocumentTypeAndDocumentNumber(
                DocumentTypeJpa.valueOf(documentType.name()), documentNumber);
    }

    @Override
    public User save(User user) {
        Set<RoleJpaEntity> roleEntities = user.getRoles().stream()
                .map(this::resolveRole)
                .collect(Collectors.toSet());

        UserJpaEntity saved = userRepository.save(UserMapper.toNewEntity(user, roleEntities));
        return UserMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id).map(UserMapper::toDomain);
    }

    private RoleJpaEntity resolveRole(RoleName roleName) {
        return roleRepository.findByName(roleName.name())
                .orElseThrow(() -> new IllegalStateException(
                        "El rol '%s' no existe en el catálogo fijo (roles). Verifica la migración Flyway V1.".formatted(roleName)));
    }
}
