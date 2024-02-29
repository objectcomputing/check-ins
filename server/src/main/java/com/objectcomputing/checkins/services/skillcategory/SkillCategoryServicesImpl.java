package com.objectcomputing.checkins.services.skillcategory;

import jakarta.inject.Singleton;

import java.util.UUID;

@Singleton
public class SkillCategoryServicesImpl implements SkillCategoryServices {

    @Override
    public SkillCategory read(UUID id) {
        return new SkillCategory(UUID.randomUUID(), "Languages", "Programming Languages");
    }
}
