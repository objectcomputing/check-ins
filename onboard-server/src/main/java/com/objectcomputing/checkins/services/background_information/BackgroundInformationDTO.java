package com.objectcomputing.checkins.services.background_information;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;


@Introspected
public class BackgroundInformationDTO {
    @NotNull
    @Schema(description = "id of the background information profile entry is associated with", required = true)
    private UUID id;

    @NotNull
    @Schema(description = "user id of the background information profile entry is associated with", required = true)
    private String userId;

    @NotNull
    @Schema(description = "boolean that indicates whether the step was completed")
    private Boolean stepComplete;

    public UUID getId() { return id;}

    public void setId(@NotNull UUID id){this.id = id;}

    @NotNull
    public String getUserId() { return userId;}

    public void setUserId(@NotNull String userId) { this.userId = userId;}

    @NotNull
    public Boolean getStepComplete() { return stepComplete;}

    public void setStepComplete(@NotNull Boolean stepComplete) { this.stepComplete = stepComplete;}

    @Override
    public String toString(){
        return "BackgroundInformationDTO{" +
                "id=" + id +
                ", userId = '" + userId + '\'' +
                ", stepComplete='" + stepComplete + '\'' +
                '}';
    }
}
