package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.skillcategory_skill.SkillCategorySkill;

import java.util.UUID;

public interface SkillCategorySkillFixture extends RepositoryFixture {

    default SkillCategorySkill createSkillCategorySkill(UUID skillCategoryId, UUID skillId) {
        SkillCategorySkill skillCategorySkill = new SkillCategorySkill(skillCategoryId, skillId);
        return getSkillCategorySkillRepository().save(skillCategorySkill);
    }

}
