package com.objectcomputing.checkins.services.guild;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Introspected
public class GuildCreateDTO {
    @Setter
    @NotBlank
    @Schema(required = true, description = "name of the guild")
    private String name;

    @Nullable
    @Schema(description = "description of the guild")
    private String description;

    @Schema(description = "members of this guild")
    private List<GuildMemberCreateDTO> guildMembers;

    @Nullable
    @Schema(description="link to the homepage of the guild")
    private String link;

    @NotNull
    @Schema(description = "Is the guild a community")
    private Boolean isCommunity;

    public GuildCreateDTO(String name, @Nullable String description, @Nullable String link) {
        this.name = name;
        this.description = description;
        this.link =link;
    }

    @Data
    @NoArgsConstructor
    @Introspected
    public static class GuildMemberCreateDTO {

        @Schema(description = "whether member is lead or not represented by true or false respectively",
                nullable = true)
        private Boolean lead;

        @NotNull
        @Schema(description = "Member who is on this guild")
        private UUID memberId;

        public GuildMemberCreateDTO(UUID memberId, Boolean lead) {
            this.memberId = memberId;
            this.lead = lead;
        }
    }
}
