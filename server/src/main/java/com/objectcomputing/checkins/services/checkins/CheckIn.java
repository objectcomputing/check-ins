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

    public CheckIn(UUID teamMemberId, UUID pdlId, LocalDate checkInDate) {
        this.teamMemberId= teamMemberId;
        this.pdlId=pdlId;
        this.checkInDate=checkInDate;
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
    @TypeDef(type=DataType.STRING)
    private UUID pdlId;

    @Column(name="checkInDate")
    private LocalDate checkInDate;

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


}