package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.skills.Skill;

public interface SkillFixture extends RepositoryFixture {

    default Skill createSkill(String name, boolean isPending, String description, boolean isExtraneous) {
        return getSkillRepository().save(
                new Skill(name, isPending, description, isExtraneous)
        );
    }

    default Skill createADefaultSkill() {
        return createSkill("Limb regeneration", true, "Regenerate a lost limb", false);
    }

    default Skill createASecondarySkill() {
        return createSkill("Limb restoration", true, "Restore a lost limb", false);
    }
}
