package com.objectcomputing.checkins.services.pulseresponse;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Introspected
public class PulseResponseCreateDTO {

    @NotNull
    @Schema(required = true, description = "date for submissionDate")
    private LocalDate submissionDate;

    @NotNull
    @Schema(required = true, description = "date for updatedDate")
    private LocalDate updatedDate;
    
    @NotNull
    @Schema(required = true, description = "id of the associated member")
    private UUID teamMemberId;

    @NotNull
    @Schema(required = true, description = "description of internal feelings")
    private String internalFeelings;

    @NotNull
    @Schema(required = true, description = "description of external feelings")
    private String externalFeelings;

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public LocalDate getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDate updatedDate) {
        this.updatedDate = updatedDate;
    }

    public UUID getTeamMemberId() {
        return teamMemberId;
    }

    public void setTeamMemberId(UUID teamMemberId) {
        this.teamMemberId = teamMemberId;
    }

    public String getInternalFeelings() {
        return internalFeelings;
    }

    public void setInternalFeelings(String internalFeelings) {
        this.internalFeelings = internalFeelings;
    }

    public String getExternalFeelings() {
        return externalFeelings;
    }

    public void setExternalFeelings(String externalFeelings) {
        this.externalFeelings = externalFeelings;
    }
}
