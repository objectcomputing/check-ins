package com.objectcomputing.checkins.services.guild.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "guildMembers")
public class GuildMember {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of this member to guild entry")
    private UUID id;

    @Column(name = "guildid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the guild this entry is associated with")
    private UUID guildid;

    @Column(name = "memberid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the member this entry is associated with")
    private UUID memberid;

    @Column(name = "lead")
    @Schema(description = "whether member is lead or not represented by true or false respectively",
            nullable = true)
    private Boolean lead;

    public GuildMember(@JsonProperty("guildid") @TypeDef(type = DataType.STRING) @Valid @NotNull UUID guildid,
                       @JsonProperty("memberid") @TypeDef(type = DataType.STRING) @Valid @NotNull UUID memberid,
                       @JsonProperty("lead") Boolean lead) {
        this.guildid = guildid;
        this.memberid = memberid;
        this.lead = lead != null && lead;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public boolean isLead() {
        return lead != null && lead;
    }

    public void setLead(boolean lead) {
        this.lead = lead;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuildMember that = (GuildMember) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(guildid, that.guildid) &&
                Objects.equals(memberid, that.memberid) &&
                Objects.equals(lead, that.lead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, guildid, memberid, lead);
    }

    @Override
    public String toString() {
        return "GuildMember{" +
                "id=" + id +
                ", guildid=" + guildid +
                ", memberid=" + memberid +
                ", lead=" + isLead() +
                '}';
    }
}
