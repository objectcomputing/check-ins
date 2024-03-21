package com.objectcomputing.checkins.services.skillcategory.skillcategory_skill;

import com.objectcomputing.checkins.services.skills.Skill;
import jakarta.inject.Singleton;

import javax.validation.Valid;
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
    public List<Skill> findSkillsBySkillCategoryId(String skillCategoryId) {
        List<Skill> skills = skillCategorySkillRepository.findSkillsBySkillCategoryId(skillCategoryId);
        return Objects.nonNull(skills) ? skills : Collections.emptyList();
    }

    @Override
    public SkillCategorySkill save(@Valid SkillCategorySkillId dto) {
        SkillCategorySkill skillCategorySkill = new SkillCategorySkill(dto);
        return skillCategorySkillRepository.save(skillCategorySkill);
    }

    @Override
    public void delete(SkillCategorySkillId dto) {
        skillCategorySkillRepository.deleteByIds(
                dto.getSkillCategoryId().toString(),
                dto.getSkillId().toString()
        );
    }

    @Override
    public void deleteAllByCategoryId(UUID categoryId) {
        skillCategorySkillRepository.deleteBySkillCategoryId(categoryId);
    }
}
