package com.objectcomputing.checkins.services.checkins;

import java.time.LocalDate;
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
@Table(name="checkins")
public class CheckIn {

    public CheckIn() {}

    public CheckIn(UUID teamMemberId, UUID pdlId, LocalDate checkInDate, String targetQtr, String targetYear) {
        this.teamMemberId= teamMemberId;
        this.pdlId=pdlId;
        this.checkInDate=checkInDate;
        this.targetQtr=targetQtr;
        this.targetYear=targetYear;
    }
    
    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type=DataType.STRING)
    private UUID id;

    @Column(name="teamMemberId")
    @NotNull
    @TypeDef(type=DataType.STRING)
    private UUID teamMemberId;

    @Column(name="pdlId")
    @NotNull
    private UUID pdlId;

    @Column(name="checkInDate")
    private LocalDate checkInDate;

    @Column(name="targetQtr")
    private String targetQtr;

    @Column(name="targetYear")
    private String targetYear;

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public LocalDate getCheckInDate() {
        return this.checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
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