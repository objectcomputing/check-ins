package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.skills.Skill;

public interface SkillFixture extends RepositoryFixture {

    default Skill createADefaultSkill() {
        return getSkillRepository().save(new Skill("Limb regeneration", true,
                "Regenerate a lost limb", false));
    }

    default Skill createASecondarySkill() {
        return getSkillRepository().save(new Skill("Limb restoration", true,
                "Restore a lost limb", false));
    }
}
