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

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDFromString;

@Data
@Introspected
public class TeamUpdateDTO {
    @NotNull
    private UUID id;

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
    private List<TeamMemberUpdateDTO> teamMembers;

    public TeamUpdateDTO(UUID id, String name, @Nullable String description, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
    }

    public TeamUpdateDTO(String id, String name, String description, boolean active) {
        this(nullSafeUUIDFromString(id), name, description, active);
    }

    public TeamUpdateDTO() {
        id = UUID.randomUUID();
    }

    @Data
    @NoArgsConstructor
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
    }
}
