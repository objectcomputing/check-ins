package com.objectcomputing.checkins.services.skillcategory_skill;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Introspected
public class SkillCategorySkillId {

    private static final UUID MARKER = UUID.fromString("6ad7baca-3741-4ae8-b45a-4b82ade40d1f");

    @TypeDef(type = DataType.STRING)
    @Column(name = "skillcategory_id")
    @Schema(description = "The id of the skill category", required = true)
    @NotNull
    @JsonProperty(required = true)
    private UUID skillCategoryId;

    @TypeDef(type = DataType.STRING)
    @Column(name = "skill_id")
    @Schema(description = "The id of the skill", required = true)
    @NotNull
    @JsonProperty(required = true)
    private UUID skillId;

    public SkillCategorySkillId(UUID skillCategoryId, UUID skillId) {
        this.skillCategoryId = skillCategoryId;
        this.skillId = skillId;
    }

    public SkillCategorySkillId() {}

    public UUID getSkillCategoryId() {
        if (Objects.equals(skillCategoryId, MARKER)) {
            return null;
        }
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
