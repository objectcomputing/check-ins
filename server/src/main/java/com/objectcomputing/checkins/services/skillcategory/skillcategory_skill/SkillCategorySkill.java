package com.objectcomputing.checkins.services.skillcategory.skillcategory_skill;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.MappedEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Introspected
@MappedEntity
@Table(name = "skillcategory_skills")
public class SkillCategorySkill {

    @EmbeddedId
    private final SkillCategorySkillId skillCategorySkillId;

    public SkillCategorySkill(SkillCategorySkillId skillCategorySkillId) {
        this.skillCategorySkillId = skillCategorySkillId;
    }
    public SkillCategorySkill(UUID skillCategoryId, UUID skillId ) {
        this.skillCategorySkillId = new SkillCategorySkillId(skillCategoryId, skillId);
    }

    public SkillCategorySkillId getSkillCategorySkillId() {
        return skillCategorySkillId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillCategorySkill that = (SkillCategorySkill) o;
        return Objects.equals(skillCategorySkillId, that.skillCategorySkillId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skillCategorySkillId);
    }
}
