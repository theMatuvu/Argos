package org.efrei.argosbackend.adapters.out.persistance.repository;

import org.efrei.argosbackend.adapters.out.persistance.entity.IncidentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IncidentJpaRepository extends JpaRepository<IncidentEntity, UUID> {
}
