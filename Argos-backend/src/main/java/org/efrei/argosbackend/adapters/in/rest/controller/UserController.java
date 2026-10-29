package org.efrei.argosbackend.adapters.in.rest.controller;

import jakarta.validation.Valid;
import org.efrei.argosbackend.adapters.in.rest.dto.request.CreateUserRequest;
import org.efrei.argosbackend.adapters.in.rest.dto.response.UserResponse;
import org.efrei.argosbackend.adapters.in.rest.mapper.UserRestMapper;
import org.efrei.argosbackend.domain.port.in.CreateUserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final CreateUserUseCase createUser;
    private final UserRestMapper userRestMapper;

    public UserController(CreateUserUseCase createUser, UserRestMapper userRestMapper) {
        this.createUser = createUser;
        this.userRestMapper = userRestMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@RequestBody @Valid CreateUserRequest request) {
        return userRestMapper.toUserResponse(createUser.create(userRestMapper.toCommand(request)));
    }
}
