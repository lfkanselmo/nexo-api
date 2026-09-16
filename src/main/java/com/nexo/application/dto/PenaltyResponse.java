package com.nexo.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PenaltyResponse(UUID contractId, long overdueDays, BigDecimal principalOwed, BigDecimal interestAmount, BigDecimal total) {
}
