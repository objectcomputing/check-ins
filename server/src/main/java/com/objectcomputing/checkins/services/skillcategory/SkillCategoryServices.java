package com.objectcomputing.checkins.services.skillcategory;

import java.util.List;
import java.util.UUID;

public interface SkillCategoryServices {

    SkillCategory save(SkillCategory skillCategory);

    SkillCategory read(UUID id);

    List<SkillCategory> findAll();

}
