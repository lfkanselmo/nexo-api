package com.nexo.infrastructure.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface PropertyJpaRepository extends JpaRepository<PropertyEntity, UUID> {
}
