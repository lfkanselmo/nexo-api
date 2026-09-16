package com.nexo.infrastructure.persistence;

import com.nexo.domain.model.Payment;
import com.nexo.domain.model.enums.PaymentStatus;
import com.nexo.domain.port.PaymentRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
class JpaPaymentRepository implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    JpaPaymentRepository(PaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity saved = jpaRepository.save(toEntity(payment));
        return toDomain(saved);
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return jpaRepository.findById(id).map(JpaPaymentRepository::toDomain);
    }

    @Override
    public List<Payment> findByContractId(UUID contractId) {
        return jpaRepository.findByContractId(contractId).stream().map(JpaPaymentRepository::toDomain).toList();
    }

    @Override
    public List<Payment> findPendingDueBefore(LocalDate referenceDate) {
        return jpaRepository.findByStatusAndDueDateBefore(PaymentStatus.PENDING, referenceDate).stream()
                .map(JpaPaymentRepository::toDomain)
                .toList();
    }

    private static PaymentEntity toEntity(Payment payment) {
        return new PaymentEntity(
                payment.getId(), payment.getContractId(), payment.getDueDate(), payment.getPaidDate(), payment.getAmount(), payment.getStatus());
    }

    private static Payment toDomain(PaymentEntity entity) {
        Payment payment = new Payment(entity.getId(), entity.getContractId(), entity.getDueDate(), entity.getAmount(), entity.getStatus());
        if (entity.getPaidDate() != null) {
            payment.markAsPaid(entity.getPaidDate());
        }
        return payment;
    }
}
