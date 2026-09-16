package com.nexo.domain.port;

import com.nexo.domain.model.Payment;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(UUID id);

    List<Payment> findByContractId(UUID contractId);

    List<Payment> findPendingDueBefore(LocalDate referenceDate);
}
