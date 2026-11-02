package org.efrei.argosbackend.adapters.out.persistance.repository;

import org.efrei.argosbackend.adapters.out.persistance.entity.ProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProviderJpaRepository extends JpaRepository<ProviderEntity, UUID> {
    List<ProviderEntity> findAllByOrderByNameAsc();
}
