package com.objectcomputing.checkins.services.guild.member;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class GuildMemberCreateDTO {

    @Schema(description = "whether member is lead or not represented by true or false respectively",
            nullable = true)
    private Boolean lead;

    @NotNull
    @Schema(description = "Guild to which the member belongs")
    private UUID guildid;

    @NotNull
    @Schema(description = "Member who is on this guild")
    private UUID memberid;

    public GuildMemberCreateDTO(UUID guildid, UUID memberid, Boolean lead) {
        this.guildid = guildid;
        this.memberid = memberid;
        this.lead = lead;
    }

    public Boolean getLead() {
        return lead;
    }

    public void setLead(Boolean lead) {
        this.lead = lead;
    }

    public UUID getGuildid() {
        return guildid;
    }

    public void setGuildid(UUID guildmid) {
        this.guildid = guildid;
    }

    public UUID getMemberid() {
        return memberid;
    }

    public void setMemberid(UUID memberid) {
        this.memberid = memberid;
    }
}
