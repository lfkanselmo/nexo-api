package com.nexo.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record TenantRequest(@NotBlank String fullName, @NotBlank @Email String email, @NotBlank String documentId) {
}
