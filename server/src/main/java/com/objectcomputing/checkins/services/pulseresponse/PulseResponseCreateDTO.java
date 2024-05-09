package com.objectcomputing.checkins.services.pulseresponse;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;
import java.time.LocalDate;

@Introspected
public class PulseResponseCreateDTO {

    @NotNull
    @Schema(required = true, description = "integer value of internal score")
    private Integer internalScore;

    @NotNull
    @Schema(required = true, description = "integer value of external score")
    private Integer externalScore;

    @NotNull
    @Schema(required = true, description = "date for submissionDate")
    private LocalDate submissionDate;
    
    @NotNull
    @Schema(required = true, description = "id of the associated member")
    private UUID teamMemberId;

    @NotNull
    @Schema(required = true, description = "description of internal feelings")
    private String internalFeelings;

    @NotNull
    @Schema(required = true, description = "description of external feelings")
    private String externalFeelings;

    public Integer getInternalScore() {
        return internalScore;
    }

    public void setInternalScore(Integer internalScore) {
        this.internalScore = internalScore;
    }

    public Integer getExternalScore() {
        return externalScore;
    }

    public void setExternalScore(Integer externalScore) {
        this.externalScore = externalScore;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
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
