package com.objectcomputing.checkins.services.guild.member;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Introspected
public class GuildMemberUpdateDTO {

    @Schema(description = "ID of the entity to update")
    private UUID id;

    @Schema(description = "whether member is lead or not represented by true or false respectively",
            nullable = true)
    private Boolean lead;

    @NotNull
    @Schema(description = "Guild to which the member belongs")
    private UUID guildId;

    @NotNull
    @Schema(description = "Member who is on this guild")
    private UUID memberId;

    public GuildMemberUpdateDTO(UUID id, UUID guildId, UUID memberId, Boolean lead) {
        this.id = id;
        this.guildId = guildId;
        this.memberId = memberId;
        this.lead = lead;
    }
}