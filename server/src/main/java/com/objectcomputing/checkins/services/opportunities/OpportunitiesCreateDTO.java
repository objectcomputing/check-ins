package com.objectcomputing.checkins.services.opportunities;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;
import java.time.LocalDate;

@Introspected
public class OpportunitiesCreateDTO {

    @NotNull
    @Schema(required = true, description = "description of internal feelings")
    private String name;

    @NotNull
    @Schema(required = true, description = "description of external feelings")
    private String description;

    @NotNull
    @Schema(required = true, description = "date for submittedOn")
    private LocalDate submittedOn;

    @NotNull
    @Schema(required = true, description = "id of the associated member")
    private UUID submittedBy;

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getSubmittedOn() {
        return submittedOn;
    }

    public void setSubmittedOn(LocalDate submittedOn) {
        this.submittedOn = submittedOn;
    }

    public UUID getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(UUID submittedBy) {
        this.submittedBy = submittedBy;
    }

}
