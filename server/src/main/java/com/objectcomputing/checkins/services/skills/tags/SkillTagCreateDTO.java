package com.objectcomputing.checkins.services.skills.tags;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

@Introspected
public class SkillTagCreateDTO {
    @Schema(description = "name of the tag", required = true)
    @NotBlank
    private String name;

    @Schema(description = "skills tagged by this")
    private List<UUID> skills;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UUID> getSkills() {
        return skills;
    }

    public void setSkills(List<UUID> skills) {
        this.skills = skills;
    }
}
