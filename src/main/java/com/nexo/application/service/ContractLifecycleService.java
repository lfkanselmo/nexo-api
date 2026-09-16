package com.nexo.application.service;

import com.nexo.application.dto.ContractRequest;
import com.nexo.domain.exception.ContractNotFoundException;
import com.nexo.domain.exception.PropertyNotFoundException;
import com.nexo.domain.exception.TenantNotFoundException;
import com.nexo.domain.model.LeaseContract;
import com.nexo.domain.model.enums.LeaseStatus;
import com.nexo.domain.port.ContractRepository;
import com.nexo.domain.port.PropertyRepository;
import com.nexo.domain.port.TenantRepository;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContractLifecycleService {

    private final ContractRepository contractRepository;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ContractLifecycleService(
            ContractRepository contractRepository,
            PropertyRepository propertyRepository,
            TenantRepository tenantRepository,
            ApplicationEventPublisher eventPublisher) {
        this.contractRepository = contractRepository;
        this.propertyRepository = propertyRepository;
        this.tenantRepository = tenantRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public LeaseContract create(ContractRequest request) {
        propertyRepository.findById(request.propertyId()).orElseThrow(() -> new PropertyNotFoundException(request.propertyId()));
        tenantRepository.findById(request.tenantId()).orElseThrow(() -> new TenantNotFoundException(request.tenantId()));

        LeaseContract contract = new LeaseContract(
                UUID.randomUUID(),
                request.propertyId(),
                request.tenantId(),
                request.startDate(),
                request.endDate(),
                request.monthlyRent(),
                request.dailyInterestRate(),
                LeaseStatus.DRAFT);

        return contractRepository.save(contract);
    }

    public LeaseContract findById(UUID id) {
        return contractRepository.findById(id).orElseThrow(() -> new ContractNotFoundException(id));
    }

    @Transactional
    public LeaseContract activate(UUID id) {
        return transitionTo(id, LeaseStatus.ACTIVE);
    }

    @Transactional
    public LeaseContract terminate(UUID id) {
        return transitionTo(id, LeaseStatus.TERMINATED);
    }

    @Transactional
    public LeaseContract renew(UUID id) {
        return transitionTo(id, LeaseStatus.RENEWAL);
    }

    @Transactional
    public LeaseContract markOverdue(UUID id) {
        return transitionTo(id, LeaseStatus.OVERDUE);
    }

    private LeaseContract transitionTo(UUID id, LeaseStatus newStatus) {
        LeaseContract contract = findById(id);
        contract.changeStatus(newStatus);
        LeaseContract saved = contractRepository.save(contract);
        contract.pullDomainEvents().forEach(eventPublisher::publishEvent);
        return saved;
    }
}
