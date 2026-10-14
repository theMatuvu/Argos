# adapters/in/rest/exception

Contient la **gestion globale des erreurs HTTP** : on attrape les exceptions (surtout celles de `domain/exception`) et on les transforme en réponse HTTP propre (code + message).
En général : une classe `@RestControllerAdvice` et éventuellement un DTO d'erreur.

Exemple : `GlobalExceptionHandler.java`
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(UserNotFoundException e) {
        return new ErrorResponse("USER_NOT_FOUND", e.getMessage());
    }
}
```
