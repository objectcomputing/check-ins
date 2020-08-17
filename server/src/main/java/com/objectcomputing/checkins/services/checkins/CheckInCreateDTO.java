package com.objectcomputing.checkins.services.checkins;

import java.time.LocalDate;
import java.util.UUID;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class CheckInCreateDTO {
    
    private UUID teamMemberId;

    private UUID pdlId;

    private LocalDate checkInDate;

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