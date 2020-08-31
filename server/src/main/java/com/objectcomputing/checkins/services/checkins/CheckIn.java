package com.objectcomputing.checkins.services.checkins;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name="checkins")
public class CheckIn {

    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type=DataType.STRING)
    @Schema(description = "id of the checkin", required = true)
    private UUID id;

    @Column(name="teamMemberId")
    @NotNull
    @TypeDef(type=DataType.STRING)
    @Schema(description = "id of team member", required = true)
    private UUID teamMemberId;

    @Column(name="pdlId")
    @NotNull
    @TypeDef(type=DataType.STRING)
    @Schema(description = "id of pdl", required = true)
    private UUID pdlId;

    @Column(name="checkInDate")
    @Schema(description = "date of checkin")
    private LocalDate checkInDate;

    @NotNull
    @Column(name = "completed")
    @Schema(description = "whether checkin is completed or not",
            required = true)
    private Boolean completed;

    public CheckIn() {}

    public CheckIn(UUID id,UUID teamMemberId, UUID pdlId, LocalDate checkInDate, Boolean completed) {
        this.id=id;
        this.teamMemberId= teamMemberId;
        this.pdlId=pdlId;
        this.checkInDate=checkInDate;
        this.completed=completed;
    }
    
    public CheckIn(UUID teamMemberId, UUID pdlId, LocalDate checkInDate, Boolean completed) {
        this(null, teamMemberId, pdlId, checkInDate,completed);
    }
    
    public boolean isCompleted() {
        return completed != null && completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    };

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckIn checkIn = (CheckIn) o;
        return Objects.equals(id, checkIn.id) &&
                Objects.equals(teamMemberId, checkIn.teamMemberId) &&
                Objects.equals(pdlId, checkIn.pdlId) &&
                Objects.equals(checkInDate, checkIn.checkInDate) &&
                Objects.equals(completed, checkIn.completed);
    }

    @Override
    public String toString() {
        return "CheckIn{" +
                "id=" + id +
                ", teamMemberId=" + teamMemberId +
                ", pdlId=" + pdlId +
                ", checkInDate=" + checkInDate +
                ", completed=" + completed +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, teamMemberId, pdlId, checkInDate, completed);
    }
}