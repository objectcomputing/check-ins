package com.objectcomputing.checkins.services.skillcategory_skill;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Introspected
public class SkillCategorySkillId {

    @TypeDef(type = DataType.STRING)
    @Column(name = "skillcategory_id")
    @Schema(description = "The id of the skill category", required = true)
    @JsonProperty(required = true)
    private final UUID skillCategoryId;

    @TypeDef(type = DataType.STRING)
    @Column(name = "skill_id")
    @Schema(description = "The id of the skill", required = true)
    @JsonProperty(required = true)
    private final UUID skillId;

    public SkillCategorySkillId(UUID skillCategoryId, UUID skillId) {
        this.skillCategoryId = skillCategoryId;
        this.skillId = skillId;
    }

    public UUID getSkillCategoryId() {
        return skillCategoryId;
    }

    public UUID getSkillId() {
        return skillId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillCategorySkillId that = (SkillCategorySkillId) o;
        return Objects.equals(skillCategoryId, that.skillCategoryId) && Objects.equals(skillId, that.skillId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skillCategoryId, skillId);
    }
}
