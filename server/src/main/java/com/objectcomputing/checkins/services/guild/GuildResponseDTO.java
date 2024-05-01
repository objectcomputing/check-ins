package com.objectcomputing.checkins.services.guild;

import com.objectcomputing.checkins.services.guild.member.GuildMemberResponseDTO;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Introspected
public class GuildResponseDTO {

    @NotNull
    private UUID id;

    @NotBlank
    @Schema(required = true, description = "name of the guild")
    private String name;

    @Nullable
    @Schema(description = "description of the guild")
    private String description;

    @Schema(description = "members of this guild")
    private List<GuildMemberResponseDTO> guildMembers;

    @Nullable
    @Schema(description="link to the homepage of the guild")
    private String link;

    @Schema(description = "Is the guild a community")
    private Boolean isCommunity;

    public GuildResponseDTO(UUID id, String name, @Nullable String description, @Nullable String link, Boolean isCommunity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.link = link;
        this.isCommunity = isCommunity;
    }

    public List<GuildMemberResponseDTO> getGuildMembers() {
        if (guildMembers == null) {
            guildMembers = new ArrayList<>();
        }
        return guildMembers;
    }
}
