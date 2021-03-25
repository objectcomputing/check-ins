package com.objectcomputing.checkins.services.member_skill.skillsreport;

import com.objectcomputing.checkins.exceptions.BadArgException;

import javax.validation.constraints.NotNull;

public enum SkillLevel {
    INTERESTED(1),
    NOVICE(2),
    INTERMEDIATE(3),
    ADVANCED(4),
    EXPERT(5);

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
            case "1":
                return SkillLevel.INTERESTED;
            case "2":
                return SkillLevel.NOVICE;
            case "3":
                return SkillLevel.INTERMEDIATE;
            case "4":
                return SkillLevel.ADVANCED;
            case "5":
                return SkillLevel.EXPERT;
            default:
                throw new BadArgException(String.format("Invalid skill level %s", level));
        }
    }

    public boolean greaterThanOrEqual(SkillLevel other) {
        return value >= other.getValue();
    }
}
