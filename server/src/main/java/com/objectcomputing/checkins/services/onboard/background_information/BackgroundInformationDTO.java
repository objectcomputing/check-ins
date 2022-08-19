package com.objectcomputing.checkins.services.onboard.background_information;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;


@Introspected
public class BackgroundInformationDTO {
    @NotNull
    @Schema(description = "id of the background information profile entry is associated with", required = true)
    private UUID id;

    @NotNull
    @Schema(description = "boolean that indicates whether the step was completed")
    private Boolean stepComplete;

    @NotNull
    @Schema(description ="email address of the newHire used to initialize their account")
    private String emailAddress;

    public UUID getId() { return id;}

    public void setId(@NotNull UUID id){this.id = id;}

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @NotNull
    public Boolean getStepComplete() { return stepComplete;}

    public void setStepComplete(@NotNull Boolean stepComplete) { this.stepComplete = stepComplete;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BackgroundInformationDTO that = (BackgroundInformationDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(stepComplete, that.stepComplete) && Objects.equals(emailAddress, that.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stepComplete, emailAddress);
    }

    @Override
    public String toString() {
        return "BackgroundInformationDTO{" +
                "id=" + id +
                ", stepComplete=" + stepComplete +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }
}
