package com.objectcomputing.checkins.services.skillcategory_skill;

import jakarta.inject.Singleton;

import java.util.*;

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
        List<String> skillNames = skillCategorySkillRepository.findSkillNamesBySkillCategoryId(skillCategoryId);
        return Objects.nonNull(skillNames) ? skillNames : Collections.emptyList();
    }

    @Override
    public List<SkillCategorySkill> findAllBySkillCategoryId(UUID categoryId) {
        return skillCategorySkillRepository.findAllBySkillCategoryId(categoryId);
    }
}
