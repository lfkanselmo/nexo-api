package com.nexo.domain.model;

import com.nexo.domain.model.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class Payment {

    private final UUID id;
    private final UUID contractId;
    private final LocalDate dueDate;
    private LocalDate paidDate;
    private final BigDecimal amount;
    private PaymentStatus status;

    public Payment(UUID id, UUID contractId, LocalDate dueDate, BigDecimal amount, PaymentStatus status) {
        this.id = id;
        this.contractId = contractId;
        this.dueDate = dueDate;
        this.amount = amount;
        this.status = status;
    }

    public void markAsPaid(LocalDate paidDate) {
        this.paidDate = paidDate;
        this.status = PaymentStatus.PAID;
    }

    public boolean isOverdue(LocalDate referenceDate) {
        return status == PaymentStatus.PENDING && dueDate.isBefore(referenceDate);
    }

    public UUID getId() {
        return id;
    }

    public UUID getContractId() {
        return contractId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
