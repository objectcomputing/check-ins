package com.objectcomputing.checkins.services.skillcategory_skill;

import java.util.List;
import java.util.UUID;

public interface SkillCategorySkillServices {
    List<SkillCategorySkill> findAll();

    List<String> findSkillNamesBySkillCategoryId(String skillCategoryId);

    List<SkillCategorySkill> findAllBySkillCategoryId(UUID categoryId);

    SkillCategorySkill save(SkillCategorySkill dto);

    void delete(SkillCategorySkillId dto);
}
