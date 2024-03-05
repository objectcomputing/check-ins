package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.skillcategory.SkillCategory;

import java.util.List;
import java.util.UUID;

public interface SkillCategoryFixture extends RepositoryFixture {

    default SkillCategory createDefaultSkillCategory() {
        SkillCategory skillCategory = new SkillCategory("Languages", "Programming Languages");
        return getSkillCategoryRepository().save(skillCategory);
    }

    default SkillCategory createAnotherSkillCategory() {
        SkillCategory skillCategory = new SkillCategory("Libraries", "Libraries used");
        return getSkillCategoryRepository().save(skillCategory);
    }

    default SkillCategory getSkillCategory(UUID id) {
        return getSkillCategoryRepository().findById(id).orElse(null);
    }

    default List<SkillCategory> findAll() {
        return getSkillCategoryRepository().findAll();
    }

}
