package com.objectcomputing.checkins.services.team.member;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "teamMembers")
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
    private UUID teamid;

    @NotNull
    @Column(name = "memberid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the member this entry is associated with", required = true)
    private UUID memberid;

    @Nullable
    @Column(name = "lead")
    @Schema(description = "whether member is lead or not represented by true or false respectively",
            nullable = true)
    private Boolean lead;

    public TeamMember(UUID teamid, UUID memberid, Boolean lead) {
        this(null, teamid, memberid, lead);
    }

    public TeamMember(UUID id, UUID teamid, UUID memberid, Boolean lead) {
        this.id = id;
        this.teamid = teamid;
        this.memberid = memberid;
        this.lead = lead;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTeamid() {
        return teamid;
    }

    public void setTeamid(UUID teamid) {
        this.teamid = teamid;
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
        TeamMember that = (TeamMember) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(teamid, that.teamid) &&
                Objects.equals(memberid, that.memberid) &&
                Objects.equals(lead, that.lead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, teamid, memberid, lead);
    }

    @Override
    public String toString() {
        return "TeamMember{" +
                "id=" + id +
                ", teamid=" + teamid +
                ", memberid=" + memberid +
                ", lead=" + isLead() +
                '}';
    }
}
