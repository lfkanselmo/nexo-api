package com.nexo.application.dto;

import com.nexo.domain.model.LeaseContract;
import com.nexo.domain.model.enums.LeaseStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ContractResponse(
        UUID id,
        UUID propertyId,
        UUID tenantId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal monthlyRent,
        BigDecimal dailyInterestRate,
        LeaseStatus status) {

    public static ContractResponse from(LeaseContract contract) {
        return new ContractResponse(
                contract.getId(),
                contract.getPropertyId(),
                contract.getTenantId(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getMonthlyRent(),
                contract.getDailyInterestRate(),
                contract.getStatus());
    }
}
