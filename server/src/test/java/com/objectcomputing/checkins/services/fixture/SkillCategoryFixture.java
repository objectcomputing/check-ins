package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.skillcategory.SkillCategory;

public interface SkillCategoryFixture extends RepositoryFixture {

    default SkillCategory createDefaultSkillCategory() {
        SkillCategory skillCategory = new SkillCategory("Languages", "Programming Languages");
        return getSkillCategoryRepository().save(skillCategory);
    }

}
