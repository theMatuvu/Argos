package org.efrei.argosbackend.application.usecase;
import org.efrei.argosbackend.domain.exception.UserNotFoundException;
import org.efrei.argosbackend.domain.model.User;
import org.efrei.argosbackend.domain.port.in.FindUserByUsernameUseCase;
import org.efrei.argosbackend.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;


@Service
public class FindUserByUsernameService implements FindUserByUsernameUseCase {
    private final UserRepositoryPort userRepository;
    public FindUserByUsernameService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }
}
