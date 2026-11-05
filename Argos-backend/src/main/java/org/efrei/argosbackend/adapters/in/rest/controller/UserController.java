package org.efrei.argosbackend.adapters.in.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.efrei.argosbackend.adapters.in.rest.dto.request.CreateUserRequest;
import org.efrei.argosbackend.adapters.in.rest.dto.response.UserResponse;
import org.efrei.argosbackend.adapters.in.rest.mapper.UserRestMapper;
import org.efrei.argosbackend.config.OpenApiConfig;
import org.efrei.argosbackend.domain.port.in.CreateUserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Utilisateurs", description = "Gestion des comptes")
@RequestMapping("/users")
public class UserController {

    private final CreateUserUseCase createUser;
    private final UserRestMapper userRestMapper;

    public UserController(CreateUserUseCase createUser, UserRestMapper userRestMapper) {
        this.createUser = createUser;
        this.userRestMapper = userRestMapper;
    }

    @Operation(summary = "Créer un compte utilisateur")
    @ApiResponse(responseCode = "201", description = "Compte créé")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @ApiResponse(responseCode = "403", ref = OpenApiConfig.FORBIDDEN)
    @ApiResponse(responseCode = "409", ref = OpenApiConfig.CONFLICT)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@RequestBody @Valid CreateUserRequest request) {
        return userRestMapper.toUserResponse(createUser.create(userRestMapper.toCommand(request)));
    }
}
