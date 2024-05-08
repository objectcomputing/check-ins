package com.objectcomputing.checkins.services.tags.entityTag;

import com.objectcomputing.checkins.services.tags.entityTag.EntityTag.EntityType;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Introspected
public class EntityTagCreateDTO {

    @NotNull
    @Schema(description = "the id of the entity", required = true)
    private UUID entityId;

    @NotNull
    @Schema(description = "the id of the tag", required = true)
    private UUID tagId;

    @NotNull
    @Schema(description = "the type of the entity tag", required = true)
    private EntityType type;

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public UUID getTagId() {
        return tagId;
    }

    public void setTagId(UUID tagId) {
        this.tagId = tagId;
    }


    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }
}
