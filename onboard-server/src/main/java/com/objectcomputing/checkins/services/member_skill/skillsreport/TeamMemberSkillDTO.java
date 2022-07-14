package com.objectcomputing.checkins.services.member_skill.skillsreport;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import io.micronaut.core.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Introspected
public class TeamMemberSkillDTO {
    @NotNull
    @Schema(required = true, description = "UUID of the team member profile")
    private UUID id;

    @Nullable
    @Schema(description = "Name of the team member")
    private String name;

    @NotNull
    @Schema(required = true, description = "Skills of the team member")
    private List<SkillLevelDTO> skills;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    public List<SkillLevelDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillLevelDTO> skills) {
        this.skills = skills;
    }
}

