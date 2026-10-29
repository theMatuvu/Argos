package org.efrei.argosbackend.adapters.out.persistance.adapter;

import org.efrei.argosbackend.adapters.out.persistance.mapper.UserPersistenceMapper;
import org.efrei.argosbackend.adapters.out.persistance.repository.UserJpaRepository;
import org.efrei.argosbackend.domain.model.User;
import org.efrei.argosbackend.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper userPersistenceMapper;

    public UserPersistenceAdapter(UserJpaRepository userJpaRepository, UserPersistenceMapper userPersistenceMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userPersistenceMapper = userPersistenceMapper;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username).map(userPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public User save(User user) {
        return userPersistenceMapper.toDomain(userJpaRepository.save(userPersistenceMapper.toEntity(user)));
    }

}
