package org.efrei.argosbackend.adapters.in.rest.mapper;

import org.efrei.argosbackend.adapters.in.rest.dto.response.LineResponse;
import org.efrei.argosbackend.adapters.in.rest.dto.response.LineSegmentResponse;
import org.efrei.argosbackend.adapters.in.rest.dto.response.NetworkMapResponse;
import org.efrei.argosbackend.domain.model.Line;
import org.efrei.argosbackend.domain.model.LineSegment;
import org.efrei.argosbackend.domain.model.NetworkMap;
import org.springframework.stereotype.Component;

@Component
public class NetworkRestMapper {

    private final StationRestMapper stationRestMapper;

    public NetworkRestMapper(StationRestMapper stationRestMapper) {
        this.stationRestMapper = stationRestMapper;
    }

    public NetworkMapResponse toResponse(NetworkMap networkMap) {
        return new NetworkMapResponse(
                networkMap.stations().stream().map(stationRestMapper::toResponse).toList(),
                networkMap.lines().stream().map(this::toResponse).toList()
        );
    }

    private LineResponse toResponse(Line line) {
        return new LineResponse(
                line.id(),
                line.name(),
                line.color(),
                line.textColor(),
                line.segments().stream().map(this::toResponse).toList()
        );
    }

    private LineSegmentResponse toResponse(LineSegment segment) {
        return new LineSegmentResponse(segment.from().id(), segment.to().id());
    }
}
