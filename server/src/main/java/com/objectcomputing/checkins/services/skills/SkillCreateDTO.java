package com.objectcomputing.checkins.services.skills;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Introspected
public class SkillCreateDTO {
    @NotBlank
    @Schema(description = "name of the skill")
    private String name;

    @NotNull
    @Schema(description = "whether the skill is accepted or not")
    private Boolean pending;

    @Schema(description = "the description of the skill")
    private String description;

    @NotNull
    @Schema(description = "the skill is extraneous (or not)")
    private Boolean extraneous = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isPending() {
        return pending;
    }

    public void setPending(Boolean pending) {
        this.pending = pending;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isExtraneous() {
        return extraneous;
    }

    public void setExtraneous(Boolean extraneous) {
        this.extraneous = extraneous;
    }
}
