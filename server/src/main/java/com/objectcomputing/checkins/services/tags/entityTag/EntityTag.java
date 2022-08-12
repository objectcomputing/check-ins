package com.objectcomputing.checkins.services.tags.entityTag;

import com.objectcomputing.checkins.exceptions.BadArgException;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name ="entity_tags")
public class EntityTag {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the id of the entity tag", required = true)
    private UUID id;

    @NotNull
    @TypeDef(type = DataType.STRING)
    @Column(name = "entity_id")
    @Schema(description = "the id of the entity", required = true)
    private UUID entityId;

    @NotNull
    @Column(name = "type")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the type of the entity being tagged", required = true)
    private EntityType type;

    @NotNull
    @TypeDef(type = DataType.STRING)
    @Column(name = "tag_id")
    @Schema(description = "the id of the tag", required = true)
    private UUID tagId;

    public enum EntityType {
        SKILL(0),
        TEAM(1);

        private final int value;

        EntityType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static EntityType convertFromString(@NotNull String type) {
            final String typelc = type.toLowerCase();
            switch (typelc) {
                case "skill":
                    return EntityType.SKILL;
                case "team":
                    return EntityType.TEAM;
                default:
                    throw new BadArgException("Invalid type %s", type);
            }
        }
    }

    public EntityTag(UUID id, @NotNull UUID entityId, @NotNull UUID tagId, @NotNull EntityType type) {
        this.id = id;
        this.entityId = entityId;
        this.type = type;
        this.tagId = tagId;
    }

    public EntityTag(@NotNull UUID entityId, @NotNull UUID tagId, @NotNull EntityType type) {
        this.entityId = entityId;
        this.type = type;
        this.tagId = tagId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public UUID getTagId() {
        return tagId;
    }

    public void setTagId(UUID tagId) {
        this.tagId = tagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityTag entityTag = (EntityTag) o;
        return id.equals(entityTag.id) &&
                entityId.equals(entityTag.entityId) &&
                type.equals(entityTag.type) &&
                tagId.equals(entityTag.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, entityId, type, tagId);
    }
}
