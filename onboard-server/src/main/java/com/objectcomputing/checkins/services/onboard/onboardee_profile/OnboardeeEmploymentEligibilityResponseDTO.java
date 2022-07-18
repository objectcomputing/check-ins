package com.objectcomputing.checkins.services.checkins.onboard;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;


@Introspected
public class OnboardeeEmploymentEligibilityResponseDTO {

    @NotNull
    @Schema(description = "id of the onboardee this profile entry is associated with", required = true)
    private UUID id;
    
    @NotNull
    @Schema(description = "onboardee age", required = true)
    private boolean ageLegal;

    @NotNull
    @Schema(description = "onboardee citizenship status", required = true)
    private boolean usCitizen;
    
    @Nullable
    @Schema(description = "onboardee visa status")
    private String visaStatus;

    @Nullable
    @Schema(description = "onboarde visa expiry date", nullable = true)
    private LocalDate expirationDate;

    @NotNull
    @Schema(description = "onboardee felony status", required = true)
    private boolean felonyStatus;

    @Nullable
    @Schema(description = "onboardee felony explanation", nullable = true)
    private String felonyExplanation;

    @NotNull
    public Boolean getAge() {
        return ageLegal;
    }

    public void setAge(@NotNull Boolean ageLegal) {
        this.ageLegal = ageLegal;
    }

    @NotNull
    public boolean getUsCitizen() {
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
    public LocalDate getVisaExpiryDate() {
        return expirationDate;
    }

    public void setVisaExpiry(@Nullable LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    @NotNull
    public Boolean getFelonyStatus() {
        return felonyStatus;
    }

    public void setFelonyStatus(@NotNull Boolean felonyStatus) {
        this.felonyStatus = felonyStatus;
    }

    @NotNull
    public String getFelonyExplanation(){
        return felonyExplanation;
    }

    public void setFelonyExplanation(@NotNull String felonyExplanation){
        this.felonyExplanation = felonyExplanation;
    }

    @Override
    public String toString() {
        return "EmploymentEligibilityDTO {" +
                "id=" + id +
                ", ageLegal ='" + ageLegal + '\'' +
                ", usCitizen='" + usCitizen + '\'' +
                ", visaStatus=" + visaStatus +  '\'' +
                ", expirationDate='" + expirationDate +
                ", felonyStatus='" + felonyStatus +
                ", felonyExplanation=" + felonyExplanation +  '\'' +
                '}';
    }

}
 
