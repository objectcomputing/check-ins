package com.objectcomputing.checkins.services.teammembers;

import java.sql.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;

@Entity
@Table(name ="team_member")
public class TeamMember {

    public TeamMember(UUID teamId, UUID memberId, boolean isLead) {
                        this.teamId=teamId;
                        this.memberId=memberId;
                        this.isLead=isLead;
                        }

    public TeamMember() {
    }

    @Id
    @Column(name="uuid")
    @AutoPopulated
    @TypeDef(type=DataType.STRING)
    private UUID uuid;

    @Column(name="teamId")
    @TypeDef(type=DataType.STRING)
    private UUID teamId;

    @Column(name="memberId")
    @TypeDef(type=DataType.STRING)
    private UUID memberId;

    @Column(name="isLead")
    @TypeDef(type=DataType.BOOLEAN)
    private boolean isLead;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

    public boolean getIsLead() {
        return isLead;
    }

    public void setIsLead(boolean isLead) {
        this.isLead = isLead;
    }
    
}
