package com.fcv.citas.auth.application;

import com.fcv.citas.auth.domain.exception.DocumentAlreadyUsedException;
import com.fcv.citas.auth.domain.exception.EmailAlreadyUsedException;
import com.fcv.citas.auth.domain.model.DocumentType;
import com.fcv.citas.auth.domain.model.RoleName;
import com.fcv.citas.auth.domain.model.User;
import com.fcv.citas.auth.domain.port.in.RegisterUserCommand;
import com.fcv.citas.auth.domain.port.out.PasswordHasherPort;
import com.fcv.citas.auth.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Cubre CA-01 a CA-04 de HU-001 (Registrar usuario). */
class RegisterUserServiceTest {

    private UserRepositoryPort userRepository;
    private PasswordHasherPort passwordHasher;
    private RegisterUserService service;

    private static final RegisterUserCommand VALID_COMMAND = new RegisterUserCommand(
            "Ana", "Pérez", DocumentType.CC, "1000000001", "ana.perez@example.com", "3000000000", "S3cret123!");

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepositoryPort.class);
        passwordHasher = mock(PasswordHasherPort.class);
        service = new RegisterUserService(userRepository, passwordHasher);
    }

    @Test
    void registersUserWithHashedPasswordAndUserRole() {
        when(userRepository.existsByEmail(VALID_COMMAND.email())).thenReturn(false);
        when(userRepository.existsByDocument(VALID_COMMAND.documentType(), VALID_COMMAND.documentNumber())).thenReturn(false);
        when(passwordHasher.hash(VALID_COMMAND.rawPassword())).thenReturn("hashed-value");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = service.register(VALID_COMMAND);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedArgument = captor.getValue();

        assertThat(savedArgument.getPasswordHash()).isEqualTo("hashed-value");
        assertThat(savedArgument.getRoles()).containsExactly(RoleName.USER);
        assertThat(savedArgument.isActive()).isTrue();
        assertThat(result.getEmail()).isEqualTo(VALID_COMMAND.email());
    }

    @Test
    void rejectsDuplicateEmail() {
        when(userRepository.existsByEmail(VALID_COMMAND.email())).thenReturn(true);

        assertThatThrownBy(() -> service.register(VALID_COMMAND))
                .isInstanceOf(EmailAlreadyUsedException.class);

        verify(userRepository, never()).save(any());
        verify(passwordHasher, never()).hash(anyString());
    }

    @Test
    void rejectsDuplicateDocument() {
        when(userRepository.existsByEmail(VALID_COMMAND.email())).thenReturn(false);
        when(userRepository.existsByDocument(VALID_COMMAND.documentType(), VALID_COMMAND.documentNumber())).thenReturn(true);

        assertThatThrownBy(() -> service.register(VALID_COMMAND))
                .isInstanceOf(DocumentAlreadyUsedException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void neverPersistsRawPassword() {
        when(userRepository.existsByEmail(VALID_COMMAND.email())).thenReturn(false);
        when(userRepository.existsByDocument(VALID_COMMAND.documentType(), VALID_COMMAND.documentNumber())).thenReturn(false);
        when(passwordHasher.hash(VALID_COMMAND.rawPassword())).thenReturn("hashed-value");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.register(VALID_COMMAND);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPasswordHash()).isNotEqualTo(VALID_COMMAND.rawPassword());
    }
}
