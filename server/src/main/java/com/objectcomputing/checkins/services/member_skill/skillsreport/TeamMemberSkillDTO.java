package com.objectcomputing.checkins.services.member_skill.skillsreport;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Introspected
public class TeamMemberSkillDTO {
    @NotNull
    @Schema(description = "UUID of the team member profile")
    private UUID id;

    @Nullable
    @Schema(description = "Name of the team member")
    private String name;

    @NotNull
    @Schema(description = "Skills of the team member")
    private List<SkillLevelDTO> skills;

}

