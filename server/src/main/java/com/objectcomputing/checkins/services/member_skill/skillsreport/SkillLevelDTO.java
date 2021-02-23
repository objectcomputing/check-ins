package com.objectcomputing.checkins.services.member_skill.skillsreport;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.annotation.Nullable;
import java.util.UUID;

@Introspected
public class SkillLevelDTO {
    @NotNull
    @Schema(required = true, description = "UUID of the skill")
    private UUID id;

    @Nullable
    @Schema(description = "Level of the skill")
    private String level;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Nullable
    public String getLevel() {
        return level;
    }

    public void setLevel(@Nullable String level) {
        this.level = level;
    }
}
