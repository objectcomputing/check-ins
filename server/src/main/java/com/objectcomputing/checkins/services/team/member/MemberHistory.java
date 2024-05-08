package com.objectcomputing.checkins.services.team.member;

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

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


@Entity
@Table(name = "member_history")
public class MemberHistory {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of this member to Member history entry", required = true)
    private UUID id;

    @NotNull
    @Column(name = "teamid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the team this entry is associated with", required = true)
    private UUID teamId;

    @NotNull
    @Column(name = "memberid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the member this entry is associated with", required = true)
    private UUID memberId;

    @Nullable
    @Column(name = "change")
    @Schema(description = "The type of change that is occurring to the team member.", required = true)
    private String change;

    @Nullable
    @Column(name = "date")
    @Schema(description = "The date of the latest change to the team member.", required = true)
    private LocalDateTime date;

    public MemberHistory(@NotNull UUID teamId, @NotNull UUID memberId, @Nullable String change, @Nullable LocalDateTime date) {
        this.teamId = teamId;
        this.memberId = memberId;
        this.change = change;
        this.date = date;
    }
    public MemberHistory(@NotNull UUID Id, @NotNull UUID teamId, @NotNull UUID memberId, @Nullable String change, @Nullable LocalDateTime date) {
        this.id = id;
        this.teamId = teamId;
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

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
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
        MemberHistory that = (MemberHistory) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(teamId, that.teamId) &&
                Objects.equals(memberId, that.memberId) &&
                Objects.equals(change, that.change) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, teamId, memberId, change, date);
    }

    @Override
    public String toString() {
        return "MemberHistory{" +
                "id=" + id +
                "teamId=" + teamId +
                ", memberId=" + memberId +
                ", change='" + change + '\'' +
                ", date=" + date +
                '}';
    }
}
