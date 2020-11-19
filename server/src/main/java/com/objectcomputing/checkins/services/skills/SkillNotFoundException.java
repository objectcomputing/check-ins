package com.objectcomputing.checkins.services.skills;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SkillNotFoundException extends RuntimeException {
    private final List<UUID> missingSkills;

    public SkillNotFoundException(String message){
        super(message);
        missingSkills = new ArrayList<>();
    }

    public SkillNotFoundException(List<UUID> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public List<UUID> getMissingSkills() {
        return missingSkills;
    }
}
