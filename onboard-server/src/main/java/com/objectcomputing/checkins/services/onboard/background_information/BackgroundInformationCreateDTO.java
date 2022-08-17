package com.objectcomputing.checkins.services.onboard.background_information;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Introspected
public class BackgroundInformationCreateDTO {

    @NotNull
    @Schema(description = "step completion indicator", required = true)
    private Boolean stepComplete;

    @NotNull
    @Schema(description ="email address of the newHire used to initialize their account")
    private String emailAddress;

    @NotNull
    public Boolean getStepComplete(){ return stepComplete;}

    public void setStepComplete(@NotNull Boolean stepComplete) { this.stepComplete = stepComplete;}

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BackgroundInformationCreateDTO that = (BackgroundInformationCreateDTO) o;
        return Objects.equals(stepComplete, that.stepComplete) && Objects.equals(emailAddress, that.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stepComplete, emailAddress);
    }
}
