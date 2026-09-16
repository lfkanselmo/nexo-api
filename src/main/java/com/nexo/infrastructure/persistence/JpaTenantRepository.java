package com.nexo.infrastructure.persistence;

import com.nexo.domain.model.Tenant;
import com.nexo.domain.port.TenantRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
class JpaTenantRepository implements TenantRepository {

    private final TenantJpaRepository jpaRepository;

    JpaTenantRepository(TenantJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Tenant save(Tenant tenant) {
        TenantEntity saved = jpaRepository.save(toEntity(tenant));
        return toDomain(saved);
    }

    @Override
    public Optional<Tenant> findById(UUID id) {
        return jpaRepository.findById(id).map(JpaTenantRepository::toDomain);
    }

    @Override
    public List<Tenant> findAll() {
        return jpaRepository.findAll().stream().map(JpaTenantRepository::toDomain).toList();
    }

    private static TenantEntity toEntity(Tenant tenant) {
        return new TenantEntity(tenant.getId(), tenant.getFullName(), tenant.getEmail(), tenant.getDocumentId());
    }

    private static Tenant toDomain(TenantEntity entity) {
        return new Tenant(entity.getId(), entity.getFullName(), entity.getEmail(), entity.getDocumentId());
    }
}
