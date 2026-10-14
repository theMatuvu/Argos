# domain/port/out

Contient les **interfaces de ce dont le métier a besoin** à l'extérieur : sauvegarder, lire en base, envoyer un mail, appeler une API…
Le domaine définit le contrat et un adapter (ex : `adapters/out/persistance/adapter`) l'implémente. Le domaine ne sait donc pas si derrière il y a PostgreSQL, un fichier ou un mock.

Exemple : `UserRepositoryPort.java`
```java
public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(Long id);
    boolean existsByEmail(String email);
}
```
