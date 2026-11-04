package org.efrei.argosbackend.domain.port.in;

import org.efrei.argosbackend.domain.model.ProviderSummary;

import java.util.List;

public interface ListProvidersUseCase {
    List<ProviderSummary> list();
}
