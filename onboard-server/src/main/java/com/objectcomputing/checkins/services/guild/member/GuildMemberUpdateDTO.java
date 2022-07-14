package com.objectcomputing.checkins.services.guild.member;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;

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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Boolean getLead() {
        return lead;
    }

    public void setLead(Boolean lead) {
        this.lead = lead;
    }

    public UUID getGuildId() {
        return guildId;
    }

    public void setGuildId(UUID guildId) {
        this.guildId = guildId;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }
}