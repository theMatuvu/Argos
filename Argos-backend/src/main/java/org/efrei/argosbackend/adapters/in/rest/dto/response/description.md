# adapters/in/rest/dto/response

Contient les **objets renvoyés en JSON au front**.
On n'y expose que ce que le client doit voir (jamais de mot de passe ni de donnée interne sensible), ce qui évite de renvoyer directement le modèle du domaine.

Exemple : `UserResponse.java`
```java
public record UserResponse(Long id, String email, String name) {}
```
