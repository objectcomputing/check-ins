package com.objectcomputing.checkins.services.skillcategory;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Introspected
public class SkillCategoryUpdateDTO {

    @NotNull
    private UUID id;

    @NotBlank
    @Schema(description = "The name of the skillcategory", required = true)
    private String name;

    @Schema(description = "The description of the skillcategory")
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
