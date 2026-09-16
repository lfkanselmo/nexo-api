package com.nexo.application.dto;

import com.nexo.domain.model.enums.InterestType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ContractRequest(
        @NotNull UUID propertyId,
        @NotNull UUID tenantId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotNull @DecimalMin("0.01") BigDecimal monthlyRent,
        @NotNull @DecimalMin("0.0") BigDecimal dailyInterestRate,
        @NotNull InterestType interestType) {
}
