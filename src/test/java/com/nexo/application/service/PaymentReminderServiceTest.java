package com.nexo.application.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexo.domain.model.LeaseContract;
import com.nexo.domain.model.Payment;
import com.nexo.domain.model.Tenant;
import com.nexo.domain.model.enums.InterestType;
import com.nexo.domain.model.enums.LeaseStatus;
import com.nexo.domain.model.enums.PaymentStatus;
import com.nexo.domain.port.ContractRepository;
import com.nexo.domain.port.PaymentRepository;
import com.nexo.domain.port.ReminderNotifier;
import com.nexo.domain.port.TenantRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentReminderServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private ReminderNotifier reminderNotifier;

    private PaymentReminderService service;

    @BeforeEach
    void setUp() {
        service = new PaymentReminderService(paymentRepository, contractRepository, tenantRepository, reminderNotifier);
    }

    private LeaseContract contract(UUID id, UUID tenantId, LeaseStatus status) {
        return new LeaseContract(
                id, UUID.randomUUID(), tenantId, LocalDate.now(), LocalDate.now().plusYears(1), new BigDecimal("1000"), new BigDecimal("0.01"), InterestType.FIXED, status);
    }

    @Test
    void sendsAReminderForEveryOverduePaymentWithAKnownContractAndTenant() {
        UUID contractId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        Payment payment = new Payment(UUID.randomUUID(), contractId, LocalDate.now().minusDays(5), new BigDecimal("1000"), PaymentStatus.PENDING);
        LeaseContract contract = contract(contractId, tenantId, LeaseStatus.ACTIVE);
        Tenant tenant = new Tenant(tenantId, "Ana Gómez", "ana@example.com", "CC123456");

        when(paymentRepository.findPendingDueBefore(any())).thenReturn(List.of(payment));
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));

        service.sendDueReminders(LocalDate.now());

        verify(reminderNotifier).remind(tenant, contract, payment);
    }

    @Test
    void skipsAPaymentWhoseContractNoLongerExists() {
        UUID contractId = UUID.randomUUID();
        Payment payment = new Payment(UUID.randomUUID(), contractId, LocalDate.now().minusDays(5), new BigDecimal("1000"), PaymentStatus.PENDING);

        when(paymentRepository.findPendingDueBefore(any())).thenReturn(List.of(payment));
        when(contractRepository.findById(contractId)).thenReturn(Optional.empty());

        service.sendDueReminders(LocalDate.now());

        verify(reminderNotifier, never()).remind(any(), any(), any());
    }

    @Test
    void skipsAPaymentBelongingToADraftContract() {
        UUID contractId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        Payment payment = new Payment(UUID.randomUUID(), contractId, LocalDate.now().minusDays(5), new BigDecimal("1000"), PaymentStatus.PENDING);

        when(paymentRepository.findPendingDueBefore(any())).thenReturn(List.of(payment));
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract(contractId, tenantId, LeaseStatus.DRAFT)));

        service.sendDueReminders(LocalDate.now());

        verify(reminderNotifier, never()).remind(any(), any(), any());
    }
}
