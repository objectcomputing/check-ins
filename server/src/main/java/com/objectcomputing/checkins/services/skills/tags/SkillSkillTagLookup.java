package com.objectcomputing.checkins.services.skills.tags;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "skill_skill_tag")
public class SkillSkillTagLookup {
    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    private UUID id;

    @Column(name = "skill_id")
    @TypeDef(type = DataType.STRING)
    private UUID skillId;

    @Column(name = "skill_tag_id")
    @TypeDef(type = DataType.STRING)
    private UUID tagId;

    public SkillSkillTagLookup(UUID skillId, UUID tagId) {
        this.skillId = skillId;
        this.tagId = tagId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSkillId() {
        return skillId;
    }

    public void setSkillId(UUID skillId) {
        this.skillId = skillId;
    }

    public UUID getTagId() {
        return tagId;
    }

    public void setTagId(UUID tagId) {
        this.tagId = tagId;
    }
}
