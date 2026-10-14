# domain/model

Contient les **objets métier purs** de l'application (le "vocabulaire" du projet).
Aucune annotation Spring ni JPA ici (`@Entity`, `@Component`… interdits) : juste du Java simple avec ses règles de validité.

Exemple : `User.java`
```java
public record User(Long id, String email, String name) {
    public User {
        if (email == null || !email.contains("@")) throw new InvalidEmailException(email);
    }
}
```
