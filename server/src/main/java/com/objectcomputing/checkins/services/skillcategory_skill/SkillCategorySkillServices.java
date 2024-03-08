package com.objectcomputing.checkins.services.skillcategory_skill;

import com.objectcomputing.checkins.services.skills.Skill;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface SkillCategorySkillServices {
    List<SkillCategorySkill> findAll();

    List<Skill> findSkillsBySkillCategoryId(String skillCategoryId);

    SkillCategorySkill save(@Valid SkillCategorySkillId dto);

    void delete(SkillCategorySkillId dto);

    void deleteAllByCategoryId(UUID categoryId);
}
