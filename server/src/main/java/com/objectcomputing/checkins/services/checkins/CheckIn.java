package com.objectcomputing.checkins.services.checkins;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Setter
@Getter
@Introspected
@Table(name = "checkins")
public class CheckIn {

    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type=DataType.STRING)
    @Schema(description = "id of the checkin")
    private UUID id;

    @Column(name="teammemberid")
    @NotNull
    @TypeDef(type=DataType.STRING)
    @Schema(description = "id of team member")
    private UUID teamMemberId;

    @Column(name="pdlid")
    @NotNull
    @TypeDef(type=DataType.STRING)
    @Schema(description = "id of pdl")
    private UUID pdlId;

    @Column(name="checkindate")
    @Schema(description = "date of checkin")
    private LocalDateTime checkInDate;

    @NotNull
    @Column(name = "completed")
    @Schema(description = "whether checkin is completed or not")
    private boolean completed;

    public CheckIn() {}

    public CheckIn(UUID id,UUID teamMemberId, UUID pdlId, LocalDateTime checkInDate, Boolean completed) {
        this.id=id;
        this.teamMemberId= teamMemberId;
        this.pdlId=pdlId;
        this.checkInDate=checkInDate;
        this.completed=completed;
    }
    
    public CheckIn(UUID teamMemberId, UUID pdlId, LocalDateTime checkInDate, Boolean completed) {
        this(null, teamMemberId, pdlId, checkInDate,completed);
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