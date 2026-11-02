package org.efrei.argosbackend.domain.port.in;

import org.efrei.argosbackend.domain.model.Provider;

import java.util.List;

public interface ListProvidersUseCase {
    List<Provider> list();
}
