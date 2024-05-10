package com.objectcomputing.checkins.services.member_skill.skillsreport;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Introspected
public class SkillsReportResponseDTO {
    @NotNull
    @Schema(description = "List of team members with requested skills")
    private List<TeamMemberSkillDTO> teamMembers;

}
