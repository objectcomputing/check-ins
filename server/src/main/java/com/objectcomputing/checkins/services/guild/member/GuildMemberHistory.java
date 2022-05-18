package com.objectcomputing.checkins.services.guild.member;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import io.micronaut.core.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name="guild_member_history")
public class GuildMemberHistory {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of this member to Guild member history entry", required = true)
    private UUID id;

    @NotNull
    @Column(name = "guildid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the guild this entry is associated with", required = true)
    private UUID guildId;

    @NotNull
    @Column(name = "memberid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the guild member this entry is associated with", required = true)
    private UUID memberId;

    @Nullable
    @Column(name = "change")
    @Schema(description = "The type of change that is occurring to the guild member.", required = true)
    private String change;

    @Nullable
    @Column(name = "date")
    @Schema(description = "The date of the latest change to the guild member.", required = true)
    private LocalDateTime date;

    public GuildMemberHistory(@NotNull UUID guildId, @NotNull UUID memberId, @Nullable String change, @Nullable LocalDateTime date) {
        this.guildId = guildId;
        this.memberId = memberId;
        this.change = change;
        this.date = date;
    }

    public GuildMemberHistory(UUID id, @NotNull UUID guildId, @NotNull UUID memberId, @Nullable String change, @Nullable LocalDateTime date) {
        this.id = id;
        this.guildId = guildId;
        this.memberId = memberId;
        this.change = change;
        this.date = date;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    @Nullable
    public String getChange() {
        return change;
    }

    public void setChange(@Nullable String change) {
        this.change = change;
    }

    @Nullable
    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(@Nullable LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuildMemberHistory that = (GuildMemberHistory) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(guildId, that.guildId) &&
                Objects.equals(memberId, that.memberId) &&
                Objects.equals(change, that.change) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, guildId, memberId, change, date);
    }

    @Override
    public String toString() {
        return "GuildMemberHistory{" +
                "id=" + id +
                ", guildId=" + guildId +
                ", guildMemberId=" + memberId +
                ", change='" + change + '\'' +
                ", date=" + date +
                '}';
    }
}
