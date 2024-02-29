package com.objectcomputing.checkins.services.skillcategory;

import jakarta.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Singleton
public class SkillCategoryServicesImpl implements SkillCategoryServices {

    private final SkillCategoryRepository skillCategoryRepository;

    public SkillCategoryServicesImpl(SkillCategoryRepository skillCategoryRepository) {
        this.skillCategoryRepository = skillCategoryRepository;
    }

    @Override
    public SkillCategory save(SkillCategory skillCategory) {
        return skillCategoryRepository.save(skillCategory);
    }

    @Override
    public SkillCategory read(@NotNull UUID id) {
        return skillCategoryRepository.findById(id).orElse(null);
    }

    @Override
    public List<SkillCategory> findAll() {
        return skillCategoryRepository.findAll();
    }
}
