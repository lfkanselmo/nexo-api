package com.nexo.infrastructure.persistence;

import com.nexo.domain.model.Property;
import com.nexo.domain.port.PropertyRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
class JpaPropertyRepository implements PropertyRepository {

    private final PropertyJpaRepository jpaRepository;

    JpaPropertyRepository(PropertyJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Property save(Property property) {
        PropertyEntity saved = jpaRepository.save(toEntity(property));
        return toDomain(saved);
    }

    @Override
    public Optional<Property> findById(UUID id) {
        return jpaRepository.findById(id).map(JpaPropertyRepository::toDomain);
    }

    @Override
    public List<Property> findAll() {
        return jpaRepository.findAll().stream().map(JpaPropertyRepository::toDomain).toList();
    }

    private static PropertyEntity toEntity(Property property) {
        return new PropertyEntity(property.getId(), property.getAddress(), property.getCity());
    }

    private static Property toDomain(PropertyEntity entity) {
        return new Property(entity.getId(), entity.getAddress(), entity.getCity());
    }
}
