package com.objectcomputing.checkins.services.team;

import com.objectcomputing.checkins.services.team.member.TeamMemberResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Introspected
public class TeamResponseDTO {
    @NotNull
    private UUID id;

    @NotBlank
    @Schema(required = true, description = "name of the team")
    private String name;

    @Nullable
    @Schema(description = "description of the team")
    private String description;

    List<TeamMemberResponseDTO> teamMembers;

    public TeamResponseDTO(UUID id, String name, @Nullable String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public TeamResponseDTO(String id, String name, @Nullable String description) {
        this(UUID.fromString(id), name, description);
    }

    public TeamResponseDTO() {
        id = UUID.randomUUID();
    }

    @Override
    public String toString() {
        return "TeamResponseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamResponseDTO that = (TeamResponseDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }

    public List<TeamMemberResponseDTO> getTeamMembers() {
        if (teamMembers == null) {
            teamMembers = new ArrayList<>();
        }
        return teamMembers;
    }

    public void setTeamMembers(List<TeamMemberResponseDTO> teamMembers) {
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
}
