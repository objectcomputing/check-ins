package com.objectcomputing.checkins.services.onboard.onboardee_employment_eligibility;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Introspected
public class OnboardeeEmploymentEligibilityCreateDTO {

    @NotNull
    @Schema(description = "onboardee age", required = true)
    private Boolean ageLegal;

    @NotNull
    @Schema(description = "onboardee citizenship status", required = true)
    private Boolean usCitizen;
    
    @Nullable
    @Schema(description = "onboardee visa status")
    private String visaStatus;

    @Nullable
    @Schema(description = "onboardee visa expiry date", nullable = true)
    private LocalDate expirationDate;

    @NotNull
    @Schema(description = "onboardee felony status", required = true)
    private Boolean felonyStatus;

    @Nullable
    @Schema(description = "onboardee felony explanation", nullable = true)
    private String felonyExplanation;

    @NotNull
    @Schema(description ="email address of the newHire used to initialize their account")
    private String emailAddress;

    @NotNull
    public Boolean getAgeLegal() {
        return ageLegal;
    }

    public void setAgeLegal(@NotNull Boolean ageLegal) {
        this.ageLegal = ageLegal;
    }

    @NotNull
    public Boolean getUsCitizen() {
        return usCitizen;
    }

    public void setUsCitizen(@NotNull Boolean usCitizen) {
        this.usCitizen = usCitizen;
    }

    @Nullable
    public String getVisaStatus() {
        return visaStatus;
    }

    public void setVisaStatus(@Nullable String visaStatus) {
        this.visaStatus = visaStatus;
    }

    @Nullable
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(@Nullable LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    @NotNull
    public Boolean getFelonyStatus() {
        return felonyStatus;
    }

    public void setFelonyStatus(@NotNull Boolean felonyStatus) {
        this.felonyStatus = felonyStatus;
    }

    @Nullable
    public String getFelonyExplanation(){
        return felonyExplanation;
    }

    public void setFelonyExplanation(@Nullable String felonyExplanation){
        this.felonyExplanation = felonyExplanation;
    }

    public String getEmailAddress() { return emailAddress; }

    public void setEmailAddress(String emailAddress) {this.emailAddress = emailAddress;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OnboardeeEmploymentEligibilityCreateDTO that = (OnboardeeEmploymentEligibilityCreateDTO) o;
        return Objects.equals(ageLegal, that.ageLegal) && Objects.equals(usCitizen, that.usCitizen) && Objects.equals(visaStatus, that.visaStatus) && Objects.equals(expirationDate, that.expirationDate) && Objects.equals(felonyStatus, that.felonyStatus) && Objects.equals(felonyExplanation, that.felonyExplanation) && Objects.equals(emailAddress, that.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ageLegal, usCitizen, visaStatus, expirationDate, felonyStatus, felonyExplanation, emailAddress);
    }
}
