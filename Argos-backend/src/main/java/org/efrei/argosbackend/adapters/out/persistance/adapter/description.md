# adapters/out/persistance/adapter

Contient les **implémentations des ports de sortie** (`domain/port/out`) pour la base de données.
C'est le pont : il reçoit un objet du domaine, le convertit en entité, appelle le repository JPA, puis reconvertit le résultat.

Exemple : `UserPersistenceAdapter.java`
```java
@Component
public class UserPersistenceAdapter implements UserRepositoryPort {
    private final UserJpaRepository jpa;
    private final UserPersistenceMapper mapper;

    public User save(User user) {
        return mapper.toDomain(jpa.save(mapper.toEntity(user)));
    }
    public boolean existsByEmail(String email) { return jpa.existsByEmail(email); }
}
```
