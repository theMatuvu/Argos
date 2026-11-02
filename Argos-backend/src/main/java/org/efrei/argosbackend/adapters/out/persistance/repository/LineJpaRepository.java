package org.efrei.argosbackend.adapters.out.persistance.repository;

import org.efrei.argosbackend.adapters.out.persistance.entity.LineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface LineJpaRepository extends JpaRepository<LineEntity, UUID> {

    @Query("""
            select distinct line from LineEntity line
            left join fetch line.segments segment
            left join fetch segment.fromStation
            left join fetch segment.toStation
            order by line.name
            """)
    List<LineEntity> findAllWithSegments();
}
