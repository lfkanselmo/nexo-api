package com.nexo.application.dto;

import com.nexo.domain.model.Tenant;
import java.util.UUID;

public record TenantResponse(UUID id, String fullName, String email, String documentId) {

    public static TenantResponse from(Tenant tenant) {
        return new TenantResponse(tenant.getId(), tenant.getFullName(), tenant.getEmail(), tenant.getDocumentId());
    }
}
