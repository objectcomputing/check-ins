package com.objectcomputing.checkins.services.team;

import com.objectcomputing.checkins.services.team.member.TeamMemberResponseDTO;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;

@Introspected
public class TeamCreateDTO {
    @NotBlank
    @Schema(required = true, description = "name of the team")
    private String name;

    @Nullable
    @Schema(description = "description of the team")
    private String description;

    @Schema(description = "members of this team")
    private List<TeamMemberResponseDTO> teamMembers;

    public TeamCreateDTO(String name, @Nullable String description) {
        this.name = name;
        this.description = description;
    }

    public TeamCreateDTO() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamCreateDTO that = (TeamCreateDTO) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

    public List<TeamMemberResponseDTO> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<TeamMemberResponseDTO> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }
}
