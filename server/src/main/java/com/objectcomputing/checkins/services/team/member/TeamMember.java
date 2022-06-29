package com.objectcomputing.checkins.services.team.member;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Introspected
@Table(name = "team_member")
public class TeamMember {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of this member to team entry", required = true)
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
    @Column(name = "lead")
    @Schema(description = "whether member is lead or not represented by true or false respectively",
            nullable = true)
    private Boolean lead;

    public TeamMember(UUID teamId, UUID memberId, Boolean lead) {
        this(null, teamId, memberId, lead);
    }

    public TeamMember(UUID id, UUID teamId, UUID memberId, Boolean lead) {
        this.id = id;
        this.teamId = teamId;
        this.memberId = memberId;
        this.lead = lead;
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
        TeamMember that = (TeamMember) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(teamId, that.teamId) &&
                Objects.equals(memberId, that.memberId) &&
                Objects.equals(lead, that.lead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, teamId, memberId, lead);
    }

    @Override
    public String toString() {
        return "TeamMember{" +
                "id=" + id +
                ", teamId=" + teamId +
                ", memberId=" + memberId +
                ", lead=" + isLead() +
                '}';
    }
}