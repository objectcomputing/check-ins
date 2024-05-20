package com.objectcomputing.checkins.services.tags.entityTag;

import com.objectcomputing.checkins.services.tags.entityTag.EntityTag.EntityType;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Introspected
public class EntityTagCreateDTO {

    @NotNull
    @Schema(description = "the id of the entity")
    private UUID entityId;

    @NotNull
    @Schema(description = "the id of the tag")
    private UUID tagId;

    @NotNull
    @Schema(description = "the type of the entity tag")
    private EntityType type;


}
