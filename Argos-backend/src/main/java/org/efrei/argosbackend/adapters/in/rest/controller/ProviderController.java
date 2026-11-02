package org.efrei.argosbackend.adapters.in.rest.controller;

import org.efrei.argosbackend.adapters.in.rest.dto.response.ProviderResponse;
import org.efrei.argosbackend.adapters.in.rest.mapper.ProviderRestMapper;
import org.efrei.argosbackend.domain.port.in.ListProvidersUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/providers")
public class ProviderController {

    private final ListProvidersUseCase listProviders;
    private final ProviderRestMapper providerRestMapper;

    public ProviderController(ListProvidersUseCase listProviders, ProviderRestMapper providerRestMapper) {
        this.listProviders = listProviders;
        this.providerRestMapper = providerRestMapper;
    }

    @GetMapping
    public List<ProviderResponse> list() {
        return listProviders.list().stream().map(providerRestMapper::toResponse).toList();
    }
}
