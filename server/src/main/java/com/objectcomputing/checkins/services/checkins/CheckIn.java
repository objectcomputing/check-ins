package com.objectcomputing.checkins.services.checkins;

import java.sql.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.micronaut.data.annotation.AutoPopulated;

@Entity
@Table(name="checkins")
public class CheckIn {

    public CheckIn() {}

    public CheckIn(UUID teamMemberId, UUID pdlId, Date checkInDate, String targetQtr, String targetYear) {
        this.teamMemberId= teamMemberId;
        this.pdlId=pdlId;
        this.checkInDate=checkInDate;
        this.targetQtr=targetQtr;
        this.targetYear=targetYear;
    }
    
    @Id
    @Column(name="uuid")
    @AutoPopulated
    private UUID id;

    @Column(name="teamMemberId")
    @NotNull
    private UUID teamMemberId;


    @Id
    @Column(name="pdlId")
    @NotNull
    private UUID pdlId;

    @Column(name="checkindate")
    private Date checkInDate;

    @Column(name="targetqtr")
    private String targetQtr;

    @Column(name="targetyear")
    private String targetYear;


    public UUID getTeamMemberId() {
        return this.teamMemberId;
    }

    public void setTeamMemberId(UUID teamMemberId) {
        this.teamMemberId = teamMemberId;
    }
   
    public UUID getPdlId() {
        return this.pdlId;
    }

    public void setPdlId(UUID pdlId) {
        this.pdlId = pdlId;
    }

    public Date getCheckInDate() {
        return this.checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getTargetQtr() {
        return this.targetQtr;
    }

    public void setTargetQtr(String targetQtr) {
        this.targetQtr = targetQtr;
    }

    public String getTargetYear() {
        return this.targetYear;
    }

    public void setTargetYear(String targetYear) {
        this.targetYear = targetYear;
    }

}