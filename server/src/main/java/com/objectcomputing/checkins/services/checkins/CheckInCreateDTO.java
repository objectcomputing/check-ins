package com.objectcomputing.checkins.services.checkins;

import java.time.LocalDate;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

@Introspected
public class CheckInCreateDTO {
    
    @NotNull
    @Schema(description = "id of team member", required = true)
    private UUID teamMemberId;

    @NotNull
    @Schema(description = "id of pdl", required = true)
    private UUID pdlId;

    @Schema(description = "date of checkin")
    private LocalDate checkInDate;

    @Schema(description = "whether checkin is completed or not", nullable = true)
    private Boolean completed;

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

    public boolean isCompleted() {
        return completed != null && completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    };

    
}