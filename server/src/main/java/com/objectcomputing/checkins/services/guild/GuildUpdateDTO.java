package com.objectcomputing.checkins.services.guild;

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
public class GuildUpdateDTO {
    @NotNull
    private UUID id;

    @NotBlank
    @Schema(description = "name of the guild")
    private String name;

    @Nullable
    @Schema(description = "description of the guild")
    private String description;

    @Nullable
    @Schema(description="link to the homepage of the guild")
    private String link;

    @Schema(description = "members of this guild")
    private List<GuildMemberUpdateDTO> guildMembers;

    @Schema(description = "Is the guild a community")
    private boolean community;

    @NotNull
    @Schema(description = "whether the guild is active")
    private boolean active;

    public GuildUpdateDTO(UUID id, String name, @Nullable String description, @Nullable String link, boolean community, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.link = link;
        this.community = community;
        this.active = active;
    }

    public GuildUpdateDTO(String id, String name, String description, @Nullable String link, boolean community, boolean active) {
        this(nullSafeUUIDFromString(id), name, description, link, community, active);
    }

    public GuildUpdateDTO() {
        id = UUID.randomUUID();
    }

    @Data
    @NoArgsConstructor
    @Introspected
    public static class GuildMemberUpdateDTO {

        @Schema(description = "ID of the entity to update")
        private UUID id;

        @Schema(description = "whether member is lead or not represented by true or false respectively",
                nullable = true)
        private Boolean lead;

        @NotNull
        @Schema(description = "Member who is on this guild")
        private UUID memberId;

        public GuildMemberUpdateDTO(UUID id, UUID memberId, Boolean lead) {
            this.id = id;
            this.memberId = memberId;
            this.lead = lead;
        }
    }
}
