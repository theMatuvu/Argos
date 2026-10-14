# adapters/in/rest/controller

Contient les **controllers REST** : les points d'entrée HTTP (`/api/users`…) appelés par le front Angular.
Un controller ne contient pas de logique métier : il reçoit un DTO, le convertit, appelle un `port/in` et renvoie un DTO de réponse.

Exemple : `UserController.java`
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final CreateUserUseCase createUser;
    private final UserRestMapper mapper;

    @PostMapping
    public UserResponse create(@RequestBody @Valid CreateUserRequest request) {
        return mapper.toResponse(createUser.create(request.email(), request.name()));
    }
}
```
