package com.objectcomputing.checkins.services.checkins;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Introspected
public class CheckInCreateDTO {
    
    @NotNull
    @Schema(description = "id of team member", required = true)
    private UUID teamMemberId;

    @NotNull
    @Schema(description = "id of pdl", required = true)
    private UUID pdlId;

    @Schema(description = "date of checkin")
    private LocalDateTime checkInDate;

    @NotNull
    @Schema(description = "whether checkin is completed or not")
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

    public LocalDateTime getCheckInDate() {
        return this.checkInDate;
    }

    public void setCheckInDate(LocalDateTime checkInDate) {
        this.checkInDate = checkInDate;
    }

    public boolean isCompleted() {
        return completed != null && completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    };
}