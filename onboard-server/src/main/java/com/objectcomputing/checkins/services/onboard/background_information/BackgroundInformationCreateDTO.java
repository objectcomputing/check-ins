package com.objectcomputing.checkins.services.onboard.background_information;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Introspected
public class BackgroundInformationCreateDTO {

    @NotNull
    @Schema(description = "user Id", required = true)
    private String userId;

    @NotNull
    @Schema(description = "step completion indicator", required = true)
    private Boolean stepComplete;

    @NotNull
    public String getUserId() { return userId; }

    public void setUserId(@NotNull String userId) { this.userId = userId;}

    @NotNull
    public Boolean getStepComplete(){ return stepComplete;}

    public void setStepComplete(@NotNull Boolean stepComplete) { this.stepComplete = stepComplete;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;;
        if (o == null || getClass() != o.getClass()) return false;
        BackgroundInformationCreateDTO that = (BackgroundInformationCreateDTO) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(stepComplete, that.stepComplete);
    }
}
