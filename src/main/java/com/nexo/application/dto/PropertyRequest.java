package com.nexo.application.dto;

import jakarta.validation.constraints.NotBlank;

public record PropertyRequest(@NotBlank String address, @NotBlank String city) {
}
