package com.objectcomputing.checkins.services.tags.entityTag;

import com.objectcomputing.checkins.services.tags.entityTag.EntityTag.EntityType;

import java.util.Set;
import java.util.UUID;

public interface EntityTagServices {

    EntityTag read(UUID uuid);

    EntityTag save(EntityTag entityTag);

    EntityTag update(EntityTag entityTag);

    Set<EntityTag> findByFields(UUID entityId, UUID tagId, EntityType type);

    void delete(UUID id);
}
