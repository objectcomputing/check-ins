package com.objectcomputing.checkins;

import java.sql.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="record_checkins")
public class CheckIns {

    public CheckIns() {}

    public CheckIns(String teamMember, UUID pdlId, Date checkInDate, int targetQtr, int targetYear) {
        this.teamMember= teamMember;
        this.pdlId=pdlId;
        this.checkInDate=checkInDate;
        this.targetQtr=targetQtr;
        this.targetYear=targetYear;
    }
    
    @Column(name="teamMember")
    private String teamMember;

    @Id
    @Column(name="uuid")
    @GeneratedValue    
    private UUID pdlId;

    @Column(name="checkindate")
    private Date checkInDate;

    @Column(name="targetqtr")
    private int targetQtr;

    @Column(name="targetyear")
    private int targetYear;

    public String getTeamMember() {
        return this.teamMember;
    }

    public void setTeamMember(String teamMember) {
        this.teamMember = teamMember;
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

    public int getTargetQtr() {
        return this.targetQtr;
    }

    public void setTargetQtr(int targetQtr) {
        this.targetQtr = targetQtr;
    }

    public int getTargetYear() {
        return this.targetYear;
    }

    public void setTargetYear(int targetYear) {
        this.targetYear = targetYear;
    }

}