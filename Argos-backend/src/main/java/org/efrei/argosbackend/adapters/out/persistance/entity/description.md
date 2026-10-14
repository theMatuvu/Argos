# adapters/out/persistance/entity

Contient les **entités JPA**, c'est-à-dire la représentation des tables de la base de données (`@Entity`, `@Table`, `@Column`…).
Elles sont différentes des objets de `domain/model` : la structure de la base peut évoluer sans impacter le métier.

Exemple : `UserEntity.java`
```java
@Entity
@Table(name = "users")
public class UserEntity {
    @Id @GeneratedValue private Long id;
    @Column(unique = true, nullable = false) private String email;
    private String name;
    // getters / setters
}
```
