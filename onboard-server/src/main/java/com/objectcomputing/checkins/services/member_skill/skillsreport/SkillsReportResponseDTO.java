package com.objectcomputing.checkins.services.member_skill.skillsreport;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.List;

@Introspected
public class SkillsReportResponseDTO {
    @NotNull
    @Schema(required = true, description = "List of team members with requested skills")
    private List<TeamMemberSkillDTO> teamMembers;

    public List<TeamMemberSkillDTO> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<TeamMemberSkillDTO> teamMembers) {
        this.teamMembers = teamMembers;
    }
}
