package com.objectcomputing.checkins.services.team;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Introspected
public class TeamCreateDTO {
    @NotBlank
    @Schema(description = "name of the team")
    private String name;

    @Nullable
    @Schema(description = "description of the team")
    private String description;

    @NotNull
    @Schema(description = "whether the team is active")
    private boolean active;

    @Schema(description = "members of this team")
    private List<TeamMemberCreateDTO> teamMembers;

    public TeamCreateDTO(String name, @Nullable String description, boolean active) {
        this.name = name;
        this.description = description;
        this.active = active;
    }

    @Data
    @NoArgsConstructor
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
    }
}
