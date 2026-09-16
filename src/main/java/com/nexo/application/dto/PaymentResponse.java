package com.nexo.application.dto;

import com.nexo.domain.model.Payment;
import com.nexo.domain.model.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PaymentResponse(UUID id, UUID contractId, LocalDate dueDate, LocalDate paidDate, BigDecimal amount, PaymentStatus status) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(), payment.getContractId(), payment.getDueDate(), payment.getPaidDate(), payment.getAmount(), payment.getStatus());
    }
}
