# adapters/out/persistance/repository

Contient les **repositories Spring Data JPA** : des interfaces qui génèrent les requêtes SQL à notre place.
Ils manipulent des `*Entity` (jamais des objets du domaine) et ne sont utilisés que par les classes de `persistance/adapter`.

Exemple : `UserJpaRepository.java`
```java
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
}
```
