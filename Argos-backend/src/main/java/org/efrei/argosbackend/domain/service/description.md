# domain/service

Contient la **logique métier** qui ne rentre pas naturellement dans un seul objet du `model` (règles qui impliquent plusieurs objets, calculs, vérifications).
Pas de Spring ni de base de données ici : un service du domaine se teste avec un simple `new`.

Exemple : `UserDomainService.java`
```java
public class UserDomainService {
    public boolean canBeDeleted(User user, List<Order> orders) {
        return orders.stream().noneMatch(Order::isPending);
    }
}
```
