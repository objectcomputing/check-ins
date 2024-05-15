package com.objectcomputing.checkins.services.member_skill.skillsreport;

import com.objectcomputing.checkins.exceptions.BadArgException;

public enum SkillLevel {
    INTERESTED(1),
    NOVICE(2),
    INTERMEDIATE(3),
    ADVANCED(4),
    EXPERT(5);

    public static final String INTERESTED_LEVEL = "1";
    public static final String NOVICE_LEVEL = "2";
    public static final String INTERMEDIATE_LEVEL = "3";
    public static final String ADVANCED_LEVEL = "4";
    public static final String EXPERT_LEVEL = "5";

    private final int value;

    SkillLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SkillLevel convertFromString(String level) {
        final String levelLc = level != null ? level.toLowerCase() : "3";
        switch (levelLc) {
            case INTERESTED_LEVEL:
                return SkillLevel.INTERESTED;
            case NOVICE_LEVEL:
                return SkillLevel.NOVICE;
            case INTERMEDIATE_LEVEL:
                return SkillLevel.INTERMEDIATE;
            case ADVANCED_LEVEL:
                return SkillLevel.ADVANCED;
            case EXPERT_LEVEL:
                return SkillLevel.EXPERT;
            default:
                throw new BadArgException(String.format("Invalid skill level %s", level));
        }
    }

    public boolean greaterThanOrEqual(SkillLevel other) {
        return value >= other.getValue();
    }
}
