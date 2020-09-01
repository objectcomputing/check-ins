package com.objectcomputing.checkins.services.skills;

import java.util.Set;
import java.util.UUID;

public interface SkillServices {

    Skill save(Skill skill);

    Skill update(Skill skill);

    Skill readSkill(UUID id);

    Set<Skill> findByValue(String name, Boolean pending);

    void delete(UUID id);
}
