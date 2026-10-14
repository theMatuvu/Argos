# adapters/in/rest/dto/request

Contient les **objets reçus dans le body des requêtes HTTP** (le JSON envoyé par le front).
On y met les validations de format (`@NotBlank`, `@Email`…) ; ce ne sont pas des objets métier.

Exemple : `CreateUserRequest.java`
```java
public record CreateUserRequest(
    @NotBlank @Email String email,
    @NotBlank String name
) {}
```
