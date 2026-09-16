package com.nexo.infrastructure.rest;

import com.nexo.application.dto.TenantRequest;
import com.nexo.application.dto.TenantResponse;
import com.nexo.domain.exception.TenantNotFoundException;
import com.nexo.domain.model.Tenant;
import com.nexo.domain.port.TenantRepository;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tenants")
class TenantController {

    private final TenantRepository tenantRepository;

    TenantController(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    TenantResponse create(@Valid @RequestBody TenantRequest request) {
        Tenant tenant = new Tenant(UUID.randomUUID(), request.fullName(), request.email(), request.documentId());
        return TenantResponse.from(tenantRepository.save(tenant));
    }

    @GetMapping("/{id}")
    ResponseEntity<TenantResponse> findById(@PathVariable UUID id) {
        Tenant tenant = tenantRepository.findById(id).orElseThrow(() -> new TenantNotFoundException(id));
        return ResponseEntity.ok(TenantResponse.from(tenant));
    }
}
