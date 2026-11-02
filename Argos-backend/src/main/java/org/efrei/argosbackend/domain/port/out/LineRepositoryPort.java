package org.efrei.argosbackend.domain.port.out;

import org.efrei.argosbackend.domain.model.Line;

import java.util.List;

public interface LineRepositoryPort {
    List<Line> findAll();
}
