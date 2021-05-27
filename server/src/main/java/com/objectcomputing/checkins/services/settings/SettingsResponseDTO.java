package com.objectcomputing.checkins.services.settings;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

@Introspected
public class SettingsResponseDTO {

    

    @NotNull
    @NotBlank
    @Schema(required = true, description = "name of the setting")
    private String name;

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
