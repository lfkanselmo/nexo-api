package com.nexo.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentRequest(@NotNull LocalDate dueDate, @NotNull @DecimalMin("0.01") BigDecimal amount) {
}
