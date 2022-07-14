package com.objectcomputing.checkins.services.team;

import com.objectcomputing.checkins.services.team.member.TeamMemberCreateDTO;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Introspected
public class TeamCreateDTO {
    @NotBlank
    @Schema(required = true, description = "name of the team")
    private String name;

    @Nullable
    @Schema(description = "description of the team")
    private String description;

    @Schema(description = "members of this team")
    private List<TeamMemberCreateDTO> teamMembers;

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

    public List<TeamMemberCreateDTO> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<TeamMemberCreateDTO> teamMembers) {
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

    @Introspected
    public static class TeamMemberCreateDTO {

        @Schema(description = "whether member is lead or not represented by true or false respectively",
                nullable = true)
        private Boolean lead;

        @NotNull
        @Schema(description = "Member who is on this team")
        private UUID memberId;

        public TeamMemberCreateDTO(UUID memberId, Boolean lead) {
            this.memberId = memberId;
            this.lead = lead;
        }

        public Boolean getLead() {
            return lead;
        }

        public void setLead(Boolean lead) {
            this.lead = lead;
        }

        public UUID getMemberId() {
            return memberId;
        }

        public void setMemberId(UUID memberId) {
            this.memberId = memberId;
        }
    }
}
