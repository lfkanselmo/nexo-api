package com.nexo.infrastructure.persistence;

import com.nexo.domain.model.enums.PaymentStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {

    List<PaymentEntity> findByContractId(UUID contractId);

    List<PaymentEntity> findByStatusAndDueDateBefore(PaymentStatus status, LocalDate referenceDate);
}
