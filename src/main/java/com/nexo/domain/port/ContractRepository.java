package com.nexo.domain.port;

import com.nexo.domain.model.LeaseContract;
import com.nexo.domain.model.enums.LeaseStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContractRepository {

    LeaseContract save(LeaseContract contract);

    Optional<LeaseContract> findById(UUID id);

    List<LeaseContract> findAll();

    List<LeaseContract> findByStatus(LeaseStatus status);
}
