package com.fcv.citas.auth.application;

import com.fcv.citas.auth.domain.exception.DocumentAlreadyUsedException;
import com.fcv.citas.auth.domain.exception.EmailAlreadyUsedException;
import com.fcv.citas.auth.domain.model.User;
import com.fcv.citas.auth.domain.port.in.RegisterUserCommand;
import com.fcv.citas.auth.domain.port.in.RegisterUserUseCase;
import com.fcv.citas.auth.domain.port.out.PasswordHasherPort;
import com.fcv.citas.auth.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

/** Implementa HU-001 (Registrar usuario). */
@Service
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;

    public RegisterUserService(UserRepositoryPort userRepository, PasswordHasherPort passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public User register(RegisterUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new EmailAlreadyUsedException(command.email());
        }
        if (userRepository.existsByDocument(command.documentType(), command.documentNumber())) {
            throw new DocumentAlreadyUsedException(command.documentType().name(), command.documentNumber());
        }

        String passwordHash = passwordHasher.hash(command.rawPassword());
        User user = User.register(
                command.firstName(),
                command.lastName(),
                command.documentType(),
                command.documentNumber(),
                command.email(),
                command.phone(),
                passwordHash
        );
        return userRepository.save(user);
    }
}
