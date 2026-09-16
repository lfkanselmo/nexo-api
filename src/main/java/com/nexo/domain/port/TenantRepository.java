package com.nexo.domain.port;

import com.nexo.domain.model.Tenant;
import java.util.Optional;
import java.util.UUID;

public interface TenantRepository {

    Tenant save(Tenant tenant);

    Optional<Tenant> findById(UUID id);
}
