package com.nexo.infrastructure.persistence;

import com.nexo.domain.model.enums.LeaseStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface LeaseContractJpaRepository extends JpaRepository<LeaseContractEntity, UUID> {

    List<LeaseContractEntity> findByStatus(LeaseStatus status);
}
