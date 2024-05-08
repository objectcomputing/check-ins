package com.objectcomputing.checkins.services.member_skill.skillsreport;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Introspected
public class SkillLevelDTO {

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
