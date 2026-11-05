package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.UsernameAlreadyUsedException;
import org.efrei.argosbackend.domain.model.HashedPassword;
import org.efrei.argosbackend.domain.model.User;
import org.efrei.argosbackend.domain.model.enums.ProfileType;
import org.efrei.argosbackend.domain.port.in.CreateUserCommand;
import org.efrei.argosbackend.domain.port.out.PasswordHasherPort;
import org.efrei.argosbackend.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateUserServiceTest {

    private static final CreateUserCommand COMMAND =
            new CreateUserCommand("Jean", "Dupont", "jdupont", "motdepasse-test", ProfileType.FIELD_AGENT);

    private final UserRepositoryPort userRepository = mock(UserRepositoryPort.class);
    private final PasswordHasherPort passwordHasher = mock(PasswordHasherPort.class);
    private final CreateUserService service = new CreateUserService(userRepository, passwordHasher);

    @Test
    void savesTheUserWithAHashedPassword() {
        HashedPassword hashedPassword = new HashedPassword("hash");
        when(userRepository.existsByUsername("jdupont")).thenReturn(false);
        when(passwordHasher.hash("motdepasse-test")).thenReturn(hashedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = service.create(COMMAND);

        assertNull(user.id());
        assertEquals("Jean", user.firstName());
        assertEquals("Dupont", user.lastName());
        assertEquals("jdupont", user.username());
        assertEquals(ProfileType.FIELD_AGENT, user.profile());
        assertEquals(hashedPassword, user.hashedPassword());
    }

    @Test
    void refusesAUsernameAlreadyUsed() {
        when(userRepository.existsByUsername("jdupont")).thenReturn(true);

        assertThrows(UsernameAlreadyUsedException.class, () -> service.create(COMMAND));
        verify(userRepository, never()).save(any());
        verify(passwordHasher, never()).hash(any());
    }
}
