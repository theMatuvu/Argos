# application/usecase

Contient les **implémentations des cas d'utilisation** (les interfaces de `domain/port/in`).
Un use case orchestre une action : il vérifie, appelle le domaine, puis utilise les `port/out` pour persister. C'est ici qu'on peut mettre `@Service` et `@Transactional`.

Exemple : `CreateUserService.java`
```java
@Service
public class CreateUserService implements CreateUserUseCase {
    private final UserRepositoryPort userRepository;

    public User create(String email, String name) {
        if (userRepository.existsByEmail(email)) throw new EmailAlreadyUsedException(email);
        return userRepository.save(new User(null, email, name));
    }
}
```
