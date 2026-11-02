package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.UsernameAlreadyUsedException;
import org.efrei.argosbackend.domain.model.User;
import org.efrei.argosbackend.domain.port.in.CreateUserCommand;
import org.efrei.argosbackend.domain.port.in.CreateUserUseCase;
import org.efrei.argosbackend.domain.port.out.PasswordHasherPort;
import org.efrei.argosbackend.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class CreateUserService implements CreateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;

    public CreateUserService(UserRepositoryPort userRepository, PasswordHasherPort passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public User create(CreateUserCommand command) {
        if (userRepository.existsByUsername(command.username())) {
            throw new UsernameAlreadyUsedException(command.username());
        }
        return userRepository.save(toUser(command));
    }

    private User toUser(CreateUserCommand command) {
        return new User(
                null,
                command.firstName(),
                command.lastName(),
                command.username(),
                command.profile(),
                passwordHasher.hash(command.password())
        );
    }
}
