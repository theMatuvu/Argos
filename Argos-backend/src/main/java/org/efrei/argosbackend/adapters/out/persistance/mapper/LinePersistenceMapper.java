package org.efrei.argosbackend.adapters.out.persistance.mapper;

import org.efrei.argosbackend.adapters.out.persistance.entity.LineEntity;
import org.efrei.argosbackend.adapters.out.persistance.entity.LineSegmentEmbeddable;
import org.efrei.argosbackend.domain.model.Line;
import org.efrei.argosbackend.domain.model.LineSegment;
import org.springframework.stereotype.Component;

@Component
public class LinePersistenceMapper {

    private final StationPersistenceMapper stationPersistenceMapper;

    public LinePersistenceMapper(StationPersistenceMapper stationPersistenceMapper) {
        this.stationPersistenceMapper = stationPersistenceMapper;
    }

    public Line toDomain(LineEntity line) {
        return new Line(
                line.getId(),
                line.getName(),
                line.getColor(),
                line.getTextColor(),
                line.getSegments().stream().map(this::toDomain).toList()
        );
    }

    private LineSegment toDomain(LineSegmentEmbeddable segment) {
        return new LineSegment(
                stationPersistenceMapper.toDomain(segment.getFromStation()),
                stationPersistenceMapper.toDomain(segment.getToStation())
        );
    }
}
