package com.objectcomputing.checkins.services.skillcategory_skill;

import java.util.List;
import java.util.UUID;

public interface SkillCategorySkillServices {
    List<SkillCategorySkill> findAll();

    List<SkillCategorySkill> findAllBySkillCategoryId(UUID categoryId);
}
