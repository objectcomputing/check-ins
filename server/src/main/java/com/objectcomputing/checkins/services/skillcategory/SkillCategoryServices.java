package com.objectcomputing.checkins.services.skillcategory;

import java.util.List;
import java.util.UUID;

public interface SkillCategoryServices {

    SkillCategory read(UUID id);

    List<SkillCategory> findAll();

}
