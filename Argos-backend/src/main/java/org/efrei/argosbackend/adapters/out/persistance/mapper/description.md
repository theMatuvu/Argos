# adapters/out/persistance/mapper

Contient les **convertisseurs entre les entités JPA et les objets du domaine**.
Les adapters les utilisent pour que le domaine ne manipule jamais un `*Entity`.

Exemple : `UserPersistenceMapper.java`
```java
@Component
public class UserPersistenceMapper {
    public User toDomain(UserEntity e) { return new User(e.getId(), e.getEmail(), e.getName()); }
    public UserEntity toEntity(User u) { /* new UserEntity() + setters */ }
}
```
