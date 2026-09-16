package com.nexo.infrastructure.scheduler;

import com.nexo.application.service.ContractLifecycleService;
import com.nexo.application.service.PaymentReminderService;
import com.nexo.domain.model.LeaseContract;
import com.nexo.domain.model.Payment;
import com.nexo.domain.model.enums.LeaseStatus;
import com.nexo.domain.port.ContractRepository;
import com.nexo.domain.port.PaymentRepository;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
class OverdueDetector {

    private final ContractRepository contractRepository;
    private final PaymentRepository paymentRepository;
    private final ContractLifecycleService lifecycleService;
    private final PaymentReminderService reminderService;

    OverdueDetector(
            ContractRepository contractRepository,
            PaymentRepository paymentRepository,
            ContractLifecycleService lifecycleService,
            PaymentReminderService reminderService) {
        this.contractRepository = contractRepository;
        this.paymentRepository = paymentRepository;
        this.lifecycleService = lifecycleService;
        this.reminderService = reminderService;
    }

    @Scheduled(cron = "${nexo.scheduler.overdue-cron}")
    void run() {
        LocalDate today = LocalDate.now();

        Set<UUID> overdueContractIds =
                paymentRepository.findPendingDueBefore(today).stream().map(Payment::getContractId).collect(Collectors.toSet());

        contractRepository.findByStatus(LeaseStatus.ACTIVE).stream()
                .map(LeaseContract::getId)
                .filter(overdueContractIds::contains)
                .forEach(lifecycleService::markOverdue);

        reminderService.sendDueReminders(today);
    }
}
