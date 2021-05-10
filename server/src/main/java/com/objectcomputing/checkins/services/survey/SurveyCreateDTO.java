package com.objectcomputing.checkins.services.survey;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;
import java.time.LocalDate;

@Introspected
public class SurveyCreateDTO {

    @NotNull
    @Schema(required = true, description = "description of internal feelings")
    private String name;

    @NotNull
    @Schema(required = true, description = "description of external feelings")
    private String description;

    @NotNull
    @Schema(required = true, description = "date for createdOn")
    private LocalDate createdOn;

    @NotNull
    @Schema(required = true, description = "id of the associated member")
    private UUID createdBy;

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

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

}
