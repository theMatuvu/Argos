package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.UserNotFoundException;
import org.efrei.argosbackend.domain.model.HashedPassword;
import org.efrei.argosbackend.domain.model.User;
import org.efrei.argosbackend.domain.model.enums.ProfileType;
import org.efrei.argosbackend.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindUserByUsernameServiceTest {

    private final UserRepositoryPort userRepository = mock(UserRepositoryPort.class);
    private final FindUserByUsernameService service = new FindUserByUsernameService(userRepository);

    @Test
    void returnsTheUser() {
        User user = new User(UUID.randomUUID(), "Jean", "Dupont", "jdupont", ProfileType.OPERATIONS_MANAGER, new HashedPassword("hash"));
        when(userRepository.findByUsername("jdupont")).thenReturn(Optional.of(user));

        assertEquals(user, service.findByUsername("jdupont"));
    }

    @Test
    void failsWhenTheUserDoesNotExist() {
        when(userRepository.findByUsername("inconnu")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.findByUsername("inconnu"));
    }
}
