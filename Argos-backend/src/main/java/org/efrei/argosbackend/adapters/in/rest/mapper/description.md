# adapters/in/rest/mapper

Contient les **convertisseurs entre les DTO REST et les objets du domaine**.
Ça garde les controllers courts et ça permet de changer le format JSON sans toucher au métier (à la main ou avec MapStruct).

Exemple : `UserRestMapper.java`
```java
@Component
public class UserRestMapper {
    public UserResponse toResponse(User user) {
        return new UserResponse(user.id(), user.email(), user.name());
    }
}
```
