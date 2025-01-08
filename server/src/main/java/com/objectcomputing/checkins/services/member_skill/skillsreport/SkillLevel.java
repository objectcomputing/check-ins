package com.objectcomputing.checkins.services.member_skill.skillsreport;

import com.objectcomputing.checkins.exceptions.BadArgException;

public enum SkillLevel {
    NONE(0),
    NOVICE(1),
    PRACTITIONER(2),
    EXPERT(3);

    public static final String NONE_LEVEL = "0";
    public static final String NOVICE_LEVEL = "1";
    public static final String PRACTITIONER_LEVEL = "2";
    public static final String EXPERT_LEVEL = "3";

    private final int value;

    SkillLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SkillLevel convertFromString(String level) {
        final String levelLc = level != null ? level.toLowerCase() : PRACTITIONER_LEVEL;
        switch (levelLc) {
            case NONE_LEVEL:
                return SkillLevel.NONE;
            case NOVICE_LEVEL:
                return SkillLevel.NOVICE;
            case PRACTITIONER_LEVEL:
                return SkillLevel.PRACTITIONER;
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
