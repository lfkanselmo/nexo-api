package com.nexo.infrastructure.persistence;

import com.nexo.domain.model.LeaseContract;
import com.nexo.domain.model.enums.LeaseStatus;
import com.nexo.domain.port.ContractRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
class JpaContractRepository implements ContractRepository {

    private final LeaseContractJpaRepository jpaRepository;

    JpaContractRepository(LeaseContractJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public LeaseContract save(LeaseContract contract) {
        LeaseContractEntity saved = jpaRepository.save(toEntity(contract));
        return toDomain(saved);
    }

    @Override
    public Optional<LeaseContract> findById(UUID id) {
        return jpaRepository.findById(id).map(JpaContractRepository::toDomain);
    }

    @Override
    public List<LeaseContract> findByStatus(LeaseStatus status) {
        return jpaRepository.findByStatus(status).stream().map(JpaContractRepository::toDomain).toList();
    }

    private static LeaseContractEntity toEntity(LeaseContract contract) {
        return new LeaseContractEntity(
                contract.getId(),
                contract.getPropertyId(),
                contract.getTenantId(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getMonthlyRent(),
                contract.getDailyInterestRate(),
                contract.getStatus());
    }

    private static LeaseContract toDomain(LeaseContractEntity entity) {
        return new LeaseContract(
                entity.getId(),
                entity.getPropertyId(),
                entity.getTenantId(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getMonthlyRent(),
                entity.getDailyInterestRate(),
                entity.getStatus());
    }
}
