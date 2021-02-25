package com.objectcomputing.checkins.services.member_skill.skillsreport;

import com.objectcomputing.checkins.exceptions.BadArgException;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.annotation.Nullable;
import java.util.UUID;

@Introspected
public class SkillLevelDTO {

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

        public static SkillLevel convertFromSkillLevel(@NotNull String level) {
            final String levelLc = level.toLowerCase();
            switch (levelLc) {
                case "interested":
                    return SkillLevelDTO.SkillLevel.INTERESTED;
                case "novice":
                    return SkillLevelDTO.SkillLevel.NOVICE;
                case "intermediate":
                    return SkillLevelDTO.SkillLevel.INTERMEDIATE;
                case "advanced":
                    return SkillLevelDTO.SkillLevel.ADVANCED;
                case "expert":
                    return SkillLevelDTO.SkillLevel.EXPERT;
                default:
                    throw new BadArgException(String.format("Invalid skill level %s", level));
            }
        }

        public boolean greaterThanOrEqual(SkillLevel other) {
            return value >= other.getValue();
        }
    }

    @NotNull
    @Schema(required = true, description = "UUID of the skill")
    private UUID id;

    @Nullable
    @Schema(description = "Level of the skill")
    private SkillLevel level;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Nullable
    public SkillLevel getLevel() {
        return level;
    }

    public void setLevel(@Nullable SkillLevel level) {
        this.level = level;
    }
}
