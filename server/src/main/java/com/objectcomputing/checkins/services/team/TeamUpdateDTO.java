package com.objectcomputing.checkins.services.team;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDFromString;

@Introspected
public class TeamUpdateDTO {
    @NotNull
    private UUID id;

    @NotBlank
    @Schema(required = true, description = "name of the team")
    private String name;

    @Nullable
    @Schema(description = "description of the team")
    private String description;

    @Schema(description = "members of this team")
    private List<TeamMemberUpdateDTO> teamMembers;

    public TeamUpdateDTO(UUID id, String name, @Nullable String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public TeamUpdateDTO(String id, String name, String description) {
        this(nullSafeUUIDFromString(id), name, description);
    }

    public TeamUpdateDTO() {
        id = UUID.randomUUID();
    }

    @Override
    public String toString() {
        return "TeamUpdateDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamUpdateDTO updateDTO = (TeamUpdateDTO) o;
        return Objects.equals(id, updateDTO.id) &&
                Objects.equals(name, updateDTO.name) &&
                Objects.equals(description, updateDTO.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }

    public List<TeamMemberUpdateDTO> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<TeamMemberUpdateDTO> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
    public static class TeamMemberUpdateDTO {
        @Schema(description = "ID of the entity to update")
        private UUID id;

        @Schema(description = "whether member is lead or not represented by true or false respectively",
                nullable = true)
        private Boolean lead;

        @NotNull
        @Schema(description = "Member who is on this team")
        private UUID memberId;

        @NotNull
        @Schema(description = "Team to which the member belongs")
        private UUID teamId;

        public TeamMemberUpdateDTO(UUID id, UUID teamId, UUID memberId, Boolean lead) {
            this.id = id;
            this.teamId = teamId;
            this.memberId = memberId;
            this.lead = lead;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
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

        public UUID getTeamId() {
            return teamId;
        }

        public void setTeamId(UUID teamId) {
            this.teamId = teamId;
        }
    }
}
