# domain/exception

Contient les **erreurs métier**, c'est-à-dire ce qui peut "mal se passer" du point de vue du métier (et non du point de vue technique).
Elles ne connaissent rien du HTTP : c'est `adapters/in/rest/exception` qui les traduit ensuite en 404, 400, etc.

Exemple : `UserNotFoundException.java`
```java
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) { super("User " + id + " not found"); }
}
```
