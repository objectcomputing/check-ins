package com.objectcomputing.checkins.services.guilds;

import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.Hidden;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
@Table(name = "guildMembers")
@IdClass(GuildMemberCompositeKey.class)
public class GuildMember {

    public GuildMember(UUID guildId, UUID memberId) {
        this(guildId, memberId, false);
    }

    public GuildMember(UUID guildId, UUID memberId, boolean isLead) {
        this.guildId = guildId;
        this.memberId = memberId;
        this.lead = isLead;
    }

    @Id
    @NotBlank
    @Column(name="guildId")
    @TypeDef(type= DataType.STRING)
    private UUID guildId;

    @Id
    @NotBlank
    @Column(name ="memberId")
    @TypeDef(type= DataType.STRING)
    private UUID memberId;

    @Column(name = "lead")
    private boolean lead;

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

    public boolean isLead() {
        return lead;
    }

    public void setLead(boolean lead) {
        this.lead = lead;
    }

    @Override
    public String toString() {
        return "GuildMember{" +
                "guildId=" + guildId +
                ", memberId=" + memberId +
                ", lead=" + lead +
                '}';
    }
}
