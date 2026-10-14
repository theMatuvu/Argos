# domain/port/in

Contient les **interfaces des cas d'utilisation** : ce que l'application *sait faire*, vu de l'extérieur.
Les controllers appellent ces interfaces (jamais directement l'implémentation) ; elles sont implémentées dans `application/usecase`.

Exemple : `CreateUserUseCase.java`
```java
public interface CreateUserUseCase {
    User create(String email, String name);
}
```
