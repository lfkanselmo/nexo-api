package com.nexo.application.service;

import com.nexo.application.dto.PenaltyResponse;
import com.nexo.application.strategy.InterestStrategy;
import com.nexo.domain.model.LeaseContract;
import com.nexo.domain.model.Payment;
import com.nexo.domain.model.enums.InterestType;
import com.nexo.domain.port.PaymentRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PenaltyCalculator {

    private final Map<InterestType, InterestStrategy> strategies;
    private final PaymentRepository paymentRepository;

    public PenaltyCalculator(List<InterestStrategy> strategies, PaymentRepository paymentRepository) {
        this.strategies = strategies.stream().collect(Collectors.toMap(InterestStrategy::type, Function.identity()));
        this.paymentRepository = paymentRepository;
    }

    public PenaltyResponse calculate(LeaseContract contract, LocalDate referenceDate) {
        InterestStrategy strategy = strategies.get(contract.getInterestType());

        List<Payment> overduePayments = paymentRepository.findByContractId(contract.getId()).stream()
                .filter(payment -> payment.isOverdue(referenceDate))
                .toList();

        BigDecimal principalOwed = BigDecimal.ZERO;
        BigDecimal interestAmount = BigDecimal.ZERO;
        long overdueDays = 0;

        for (Payment payment : overduePayments) {
            long daysLate = ChronoUnit.DAYS.between(payment.getDueDate(), referenceDate);
            principalOwed = principalOwed.add(payment.getAmount());
            interestAmount = interestAmount.add(strategy.calculate(payment.getAmount(), contract.getDailyInterestRate(), daysLate));
            overdueDays = Math.max(overdueDays, daysLate);
        }

        return new PenaltyResponse(contract.getId(), overdueDays, principalOwed, interestAmount, principalOwed.add(interestAmount));
    }
}
