package com.objectcomputing.checkins.services.skillcategory_skill;

import jakarta.inject.Singleton;

import java.util.List;
import java.util.UUID;

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

    @Override
    public List<String> findSkillNamesBySkillCategoryId(String skillCategoryId) {
        return skillCategorySkillRepository.findSkillNamesBySkillCategoryId(skillCategoryId);
    }

    @Override
    public List<SkillCategorySkill> findAllBySkillCategoryId(UUID categoryId) {
        return skillCategorySkillRepository.findAllBySkillCategoryId(categoryId);
    }
}
