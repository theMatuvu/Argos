package org.efrei.argosbackend.domain.port.in;

import org.efrei.argosbackend.domain.model.Station;

import java.util.List;

public interface ListStationsUseCase {
    List<Station> list();
}
