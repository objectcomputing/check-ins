package com.objectcomputing.checkins.services.guild.member;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Introspected
@Table(name = "guild_member")
public class GuildMember {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of this member to guild entry")
    private UUID id;

    @NotNull
    @Column(name = "guildid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the guild this entry is associated with")
    private UUID guildId;

    @NotNull
    @Column(name = "memberid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the member this entry is associated with")
    private UUID memberId;

    @Nullable
    @Column(name = "lead")
    @Schema(description = "whether member is lead or not represented by true or false respectively",
            nullable = true)
    private Boolean lead;

    public GuildMember(UUID guildId, UUID memberId, Boolean lead) {
        this(null, guildId, memberId, lead);
    }

    public GuildMember(UUID id, UUID guildId, UUID memberId, @Nullable Boolean lead) {
        this.id = id;
        this.guildId = guildId;
        this.memberId = memberId;
        this.lead = lead;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuildMember that = (GuildMember) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(guildId, that.guildId) &&
                Objects.equals(memberId, that.memberId) &&
                Objects.equals(lead, that.lead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, guildId, memberId, lead);
    }

    @Override
    public String toString() {
        return "GuildMember{" +
                "id=" + id +
                ", guildId=" + guildId +
                ", memberId=" + memberId +
                ", lead=" + getLead() +
                '}';
    }
}
