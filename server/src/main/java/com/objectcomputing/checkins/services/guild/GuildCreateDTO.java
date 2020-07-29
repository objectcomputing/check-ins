package com.objectcomputing.checkins.services.guild;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

@Introspected
public class GuildCreateDTO {
    @NotBlank
    @Schema(required = true, description = "name of the guild")
    private String name;
    @NotBlank
    @Schema(required = true, description = "description of the guild")
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
