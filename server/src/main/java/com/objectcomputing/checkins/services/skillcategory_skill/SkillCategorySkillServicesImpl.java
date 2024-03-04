package com.objectcomputing.checkins.services.skillcategory_skill;

import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class SkillCategorySkillServicesImpl implements SkillCategorySkillServices {

    private final SkillCategorySkillRepository skillCategorySkillRepository;

    public SkillCategorySkillServicesImpl(SkillCategorySkillRepository skillCategorySkillRepository) {
        this.skillCategorySkillRepository = skillCategorySkillRepository;
    }

    @Override
    public List<SkillCategorySkill> findAll() {
        return null;
    }
}
