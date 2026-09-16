package com.nexo.domain.port;

import com.nexo.domain.model.Property;
import java.util.Optional;
import java.util.UUID;

public interface PropertyRepository {

    Property save(Property property);

    Optional<Property> findById(UUID id);
}
