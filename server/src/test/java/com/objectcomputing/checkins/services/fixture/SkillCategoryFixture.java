package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.skillcategory.SkillCategory;

public interface SkillCategoryFixture extends RepositoryFixture {

    default SkillCategory createSkillCategory(String name, String description) {
        SkillCategory skillCategory = new SkillCategory(name, description);
        return getSkillCategoryRepository().save(skillCategory);
    }

    default SkillCategory createDefaultSkillCategory() {
        return createSkillCategory("Languages", "Programming Languages");
    }

    default SkillCategory createAnotherSkillCategory() {
        return createSkillCategory("Libraries", "Libraries used");
    }
}
