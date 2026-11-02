package org.efrei.argosbackend.adapters.out.persistance.repository;

import org.efrei.argosbackend.adapters.out.persistance.entity.StationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StationJpaRepository extends JpaRepository<StationEntity, UUID> {
    List<StationEntity> findAllByOrderByNameAsc();
}
