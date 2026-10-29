package org.efrei.argosbackend.adapters.in.rest.mapper;

import org.efrei.argosbackend.adapters.in.rest.dto.request.CreateUserRequest;
import org.efrei.argosbackend.adapters.in.rest.dto.response.CurrentUserResponse;
import org.efrei.argosbackend.adapters.in.rest.dto.response.UserResponse;
import org.efrei.argosbackend.domain.model.User;
import org.efrei.argosbackend.domain.port.in.CreateUserCommand;
import org.springframework.stereotype.Component;

@Component
public class UserRestMapper {

    public CurrentUserResponse toCurrentUserResponse(User user) {
        return new CurrentUserResponse(user.firstName(), user.profile());
    }

    public UserResponse toUserResponse(User user) {
        return new UserResponse(user.id(), user.username(), user.firstName(), user.lastName(), user.profile());
    }

    public CreateUserCommand toCommand(CreateUserRequest request) {
        return new CreateUserCommand(
                request.firstName(),
                request.lastName(),
                request.username(),
                request.password(),
                request.profile()
        );
    }
}
