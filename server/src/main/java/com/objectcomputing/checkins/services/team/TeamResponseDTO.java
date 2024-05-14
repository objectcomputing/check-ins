package com.objectcomputing.checkins.services.team;

import com.objectcomputing.checkins.services.team.member.TeamMemberResponseDTO;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Introspected
public class TeamResponseDTO {
    @NotNull
    private UUID id;

    @NotBlank
    @Schema(description = "name of the team")
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


    public List<TeamMemberResponseDTO> getTeamMembers() {
        if (teamMembers == null) {
            teamMembers = new ArrayList<>();
        }
        return teamMembers;
    }
}
