package com.objectcomputing.checkins;

import java.sql.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="checkins")
public class CheckIns {

    public CheckIns() {}

    public CheckIns(UUID teamMemberId, UUID pdlId, Date checkInDate, String targetQtr, String targetYear) {
        this.teamMemberId= teamMemberId;
        this.pdlId=pdlId;
        this.checkInDate=checkInDate;
        this.targetQtr=targetQtr;
        this.targetYear=targetYear;
    }
    
    @Column(name="teamMemberId")
    private UUID teamMemberId;


    @Id
    @Column(name="uuid")
    @GeneratedValue    
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