package com.objectcomputing.checkins.services.skillcategory;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.skillcategory_skill.SkillCategorySkillServices;
import com.objectcomputing.checkins.services.skills.Skill;
import jakarta.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.*;

@Singleton
public class SkillCategoryServicesImpl implements SkillCategoryServices {

    private final SkillCategoryRepository skillCategoryRepository;
    private final SkillCategorySkillServices skillCategorySkillServices;

    public SkillCategoryServicesImpl(SkillCategoryRepository skillCategoryRepository
            , SkillCategorySkillServices skillCategorySkillServices) {
        this.skillCategoryRepository = skillCategoryRepository;
        this.skillCategorySkillServices = skillCategorySkillServices;
    }

    @Override
    public SkillCategory save(SkillCategory skillCategory) {
        if (skillCategoryRepository.findByName(skillCategory.getName()).isPresent()) {
            throw new AlreadyExistsException(skillCategory.getName());
        }
        return skillCategoryRepository.save(skillCategory);
    }

    @Override
    public SkillCategoryResponseDTO read(@NotNull UUID id) {
        SkillCategory skillCategory = skillCategoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Category not found")
        );

        return getSkillCategoryResponseDTO(skillCategory);
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
            SkillCategoryResponseDTO dto = getSkillCategoryResponseDTO(category);
            categoriesWithSkills.add(dto);
        }

        return categoriesWithSkills;
    }

    @Override
    public SkillCategory update(SkillCategory skillCategory) {
        if (skillCategoryRepository.findById(skillCategory.getId()).isEmpty()) {
            throw new BadArgException(String.format("Category with %s does not exist", skillCategory.getId()));
        }
        return skillCategoryRepository.update(skillCategory);
    }

    // TODO: Investigate transactional
    @Override
    public void delete(UUID id) {
        skillCategorySkillServices.deleteAllByCategoryId(id);
        skillCategoryRepository.deleteById(id);
    }

    private SkillCategoryResponseDTO getSkillCategoryResponseDTO(SkillCategory category) {
        List<Skill> skills = skillCategorySkillServices.findSkillsBySkillCategoryId(category.getId().toString());
        return SkillCategoryResponseDTO.create(category, skills);
    }
}
