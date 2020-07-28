package com.objectcomputing.checkins.services.guilds;

import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class GuildMemberCompositeKey implements Serializable {
    @NotBlank
    @TypeDef(type= DataType.STRING)
    private UUID guildId;

    @NotBlank
    @TypeDef(type= DataType.STRING)
    private UUID memberId;

    public GuildMemberCompositeKey(@NotBlank UUID guildId, @NotBlank UUID memberId) {
        this.guildId = guildId;
        this.memberId = memberId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuildMemberCompositeKey that = (GuildMemberCompositeKey) o;
        return guildId.equals(that.guildId) &&
                memberId.equals(that.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guildId, memberId);
    }
}
