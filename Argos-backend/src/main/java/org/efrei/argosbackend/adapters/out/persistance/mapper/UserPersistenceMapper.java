package org.efrei.argosbackend.adapters.out.persistance.mapper;

import org.efrei.argosbackend.adapters.out.persistance.entity.UserEntity;
import org.efrei.argosbackend.domain.model.HashedPassword;
import org.efrei.argosbackend.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getUsername(),
                entity.getProfile(),
                new HashedPassword(entity.getPasswordHash())
        );
    }

    public UserEntity toEntity(User user) {
        return new UserEntity(
                user.id(),
                user.username(),
                user.hashedPassword().value(),
                user.firstName(),
                user.lastName(),
                user.profile()
        );
    }

}
