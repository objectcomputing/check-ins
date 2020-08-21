package com.objectcomputing.checkins.services.skills;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

@Introspected
public class SkillCreateDTO {
    @NotBlank
    @Schema(required = true, description = "name of the guild")
    private String name;

    @NotBlank
    @Schema(required = true, description = "description of the guild")
    private boolean pending;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }
}
