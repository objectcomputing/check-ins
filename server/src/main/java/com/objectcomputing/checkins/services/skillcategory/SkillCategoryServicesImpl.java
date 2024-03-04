package com.objectcomputing.checkins.services.skillcategory;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.services.skillcategory_skill.SkillCategorySkill;
import com.objectcomputing.checkins.services.skillcategory_skill.SkillCategorySkillServices;
import com.objectcomputing.checkins.services.skills.Skill;
import com.objectcomputing.checkins.services.skills.SkillServices;
import jakarta.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class SkillCategoryServicesImpl implements SkillCategoryServices {

    private final SkillCategoryRepository skillCategoryRepository;
    private final SkillCategorySkillServices skillCategorySkillServices;
    private final SkillServices skillServices;

    public SkillCategoryServicesImpl(SkillCategoryRepository skillCategoryRepository
            , SkillCategorySkillServices skillCategorySkillServices
            , SkillServices skillServices) {
        this.skillCategoryRepository = skillCategoryRepository;
        this.skillCategorySkillServices = skillCategorySkillServices;
        this.skillServices = skillServices;
    }



    @Override
    public SkillCategory save(SkillCategory skillCategory) {
        if (skillCategoryRepository.findByName(skillCategory.getName()).isPresent()) {
            throw new AlreadyExistsException(skillCategory.getName());
        }
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

    @Override
    public List<SkillCategoryResponseDTO> findAllWithSkills() {
        List<SkillCategoryResponseDTO> categoriesWithSkills = new ArrayList<>();
        List<SkillCategory> categories = skillCategoryRepository.findAll();
        for (SkillCategory category : categories) {
            List<SkillCategorySkill> skillCategorySkills = skillCategorySkillServices.findAllBySkillCategoryId(category.getId());
            List<Skill> skills = skillCategorySkills.stream().map(skillCategorySkill ->
                    skillServices.readSkill(skillCategorySkill.getSkillCategorySkillId().getSkillId())
            ).collect(Collectors.toList());
            SkillCategoryResponseDTO dto = SkillCategoryResponseDTO.create(category, skills);
            categoriesWithSkills.add(dto);
        }

        return categoriesWithSkills;
    }
}
