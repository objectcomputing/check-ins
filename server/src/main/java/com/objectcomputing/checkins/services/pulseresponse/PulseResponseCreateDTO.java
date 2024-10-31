package com.objectcomputing.checkins.services.pulseresponse;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Introspected
public class PulseResponseCreateDTO {

    @NotNull
    @Schema(required = true, description = "integer value of internal score")
    private Integer internalScore;

    @Nullable
    @Schema(description = "integer value of external score")
    private Integer externalScore;

    @NotNull
    @Schema(description = "date for submissionDate")
    private LocalDate submissionDate;
    
    @NotNull
    @Schema(description = "id of the associated member")
    private UUID teamMemberId;

    @Nullable
    @Schema(description = "description of internal feelings")
    private String internalFeelings;

    @Nullable
    @Schema(description = "description of external feelings")
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
