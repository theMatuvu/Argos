package org.efrei.argosbackend.adapters.in.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.efrei.argosbackend.adapters.in.rest.dto.response.CurrentUserResponse;
import org.efrei.argosbackend.adapters.in.rest.mapper.UserRestMapper;
import org.efrei.argosbackend.domain.port.in.FindUserByUsernameUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@Tag(name = "Authentification", description = "Session de l'utilisateur connecté")
@RequestMapping("/auth")
public class AuthController {

    private final FindUserByUsernameUseCase findUserByUsername;
    private final UserRestMapper userRestMapper;

    public AuthController(FindUserByUsernameUseCase findUserByUsername, UserRestMapper userRestMapper) {
        this.findUserByUsername = findUserByUsername;
        this.userRestMapper = userRestMapper;
    }

    @Operation(summary = "Consulter l'utilisateur connecté")
    @GetMapping("/me")
    public CurrentUserResponse currentUser(Principal principal) {
        return userRestMapper.toCurrentUserResponse(findUserByUsername.findByUsername(principal.getName()));
    }
}
