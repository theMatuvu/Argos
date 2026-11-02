package org.efrei.argosbackend.adapters.in.security;

import org.efrei.argosbackend.domain.exception.UserNotFoundException;
import org.efrei.argosbackend.domain.model.User;
import org.efrei.argosbackend.domain.port.in.FindUserByUsernameUseCase;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ArgosUserDetailsService implements UserDetailsService {

    private final FindUserByUsernameUseCase findUserByUsername;

    public ArgosUserDetailsService(FindUserByUsernameUseCase findUserByUsername) {
        this.findUserByUsername = findUserByUsername;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        try {
            return toUserDetails(findUserByUsername.findByUsername(username));
        } catch (UserNotFoundException exception) {
            throw new UsernameNotFoundException(exception.getMessage(), exception);
        }
    }

    private UserDetails toUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.username())
                .password(user.hashedPassword().value())
                .roles(user.profile().name())
                .build();
    }
}
