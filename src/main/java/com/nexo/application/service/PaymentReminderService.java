package com.nexo.application.service;

import com.nexo.domain.model.LeaseContract;
import com.nexo.domain.model.Payment;
import com.nexo.domain.model.Tenant;
import com.nexo.domain.model.enums.LeaseStatus;
import com.nexo.domain.port.ContractRepository;
import com.nexo.domain.port.PaymentRepository;
import com.nexo.domain.port.ReminderNotifier;
import com.nexo.domain.port.TenantRepository;
import java.time.LocalDate;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentReminderService {

    private static final Logger log = LoggerFactory.getLogger(PaymentReminderService.class);
    private static final Set<LeaseStatus> REMINDABLE_STATUSES = Set.of(LeaseStatus.ACTIVE, LeaseStatus.OVERDUE);

    private final PaymentRepository paymentRepository;
    private final ContractRepository contractRepository;
    private final TenantRepository tenantRepository;
    private final ReminderNotifier reminderNotifier;

    public PaymentReminderService(
            PaymentRepository paymentRepository,
            ContractRepository contractRepository,
            TenantRepository tenantRepository,
            ReminderNotifier reminderNotifier) {
        this.paymentRepository = paymentRepository;
        this.contractRepository = contractRepository;
        this.tenantRepository = tenantRepository;
        this.reminderNotifier = reminderNotifier;
    }

    public void sendDueReminders(LocalDate referenceDate) {
        for (Payment payment : paymentRepository.findPendingDueBefore(referenceDate)) {
            LeaseContract contract = contractRepository.findById(payment.getContractId()).orElse(null);
            if (contract == null || !REMINDABLE_STATUSES.contains(contract.getStatus())) {
                continue;
            }
            Tenant tenant = tenantRepository.findById(contract.getTenantId()).orElse(null);
            if (tenant == null) {
                continue;
            }
            try {
                reminderNotifier.remind(tenant, contract, payment);
            } catch (RuntimeException e) {
                log.error("No se pudo enviar el recordatorio del pago {} al inquilino {}", payment.getId(), tenant.getId(), e);
            }
        }
    }
}
