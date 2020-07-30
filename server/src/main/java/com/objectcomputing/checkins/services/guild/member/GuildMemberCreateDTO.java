package com.objectcomputing.checkins.services.guild.member;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class GuildMemberCreateDTO {
    @NotNull
    @Schema(description = "id of the guild this entry is associated with", required = true)
    private UUID guildid;

    @NotNull
    @Schema(description = "id of the member this entry is associated with", required = true)
    private UUID memberid;

    @Schema(description = "whether member is lead or not represented by true or false respectively",
            nullable = true)
    private Boolean lead;

    public UUID getGuildid() {
        return guildid;
    }

    public void setGuildid(UUID guildid) {
        this.guildid = guildid;
    }

    public UUID getMemberid() {
        return memberid;
    }

    public void setMemberid(UUID memberid) {
        this.memberid = memberid;
    }

    public Boolean getLead() {
        return lead;
    }

    public void setLead(Boolean lead) {
        this.lead = lead;
    }
}
