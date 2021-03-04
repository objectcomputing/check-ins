package com.objectcomputing.checkins.services.member_skill.skillsreport;

import com.objectcomputing.checkins.exceptions.BadArgException;

import javax.validation.constraints.NotNull;

public enum SkillLevel {
    INTERESTED(0),
    NOVICE(1),
    INTERMEDIATE(2),
    ADVANCED(3),
    EXPERT(4);

    private final int value;

    SkillLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SkillLevel convertFromString(@NotNull String level) {
        final String levelLc = level.toLowerCase();
        switch (levelLc) {
            case "interested":
                return SkillLevel.INTERESTED;
            case "novice":
                return SkillLevel.NOVICE;
            case "intermediate":
                return SkillLevel.INTERMEDIATE;
            case "advanced":
                return SkillLevel.ADVANCED;
            case "expert":
                return SkillLevel.EXPERT;
            default:
                throw new BadArgException(String.format("Invalid skill level %s", level));
        }
    }

    public boolean greaterThanOrEqual(SkillLevel other) {
        return value >= other.getValue();
    }
}
