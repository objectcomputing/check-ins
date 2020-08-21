package com.objectcomputing.checkins.services.skills;

import java.util.Set;
import java.util.UUID;

public interface SkillServices {

    Skill read(UUID uuid);

    Skill save(Skill skill);

    Set<Skill> findByValue(String name, Boolean pending);

    void delete(UUID id);
}
