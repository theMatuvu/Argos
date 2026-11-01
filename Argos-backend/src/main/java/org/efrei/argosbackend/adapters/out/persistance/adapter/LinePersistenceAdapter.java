package org.efrei.argosbackend.adapters.out.persistance.adapter;

import org.efrei.argosbackend.adapters.out.persistance.mapper.LinePersistenceMapper;
import org.efrei.argosbackend.adapters.out.persistance.repository.LineJpaRepository;
import org.efrei.argosbackend.domain.model.Line;
import org.efrei.argosbackend.domain.port.out.LineRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LinePersistenceAdapter implements LineRepositoryPort {

    private final LineJpaRepository lineJpaRepository;
    private final LinePersistenceMapper linePersistenceMapper;

    public LinePersistenceAdapter(LineJpaRepository lineJpaRepository, LinePersistenceMapper linePersistenceMapper) {
        this.lineJpaRepository = lineJpaRepository;
        this.linePersistenceMapper = linePersistenceMapper;
    }

    @Override
    public List<Line> findAll() {
        return lineJpaRepository.findAllWithSegments().stream()
                .map(linePersistenceMapper::toDomain)
                .toList();
    }
}
