package com.objectcomputing.checkins.services.checkins.onboard;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Introspected
public class onboardeeEmploymentEligibilityCreateDTO {

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
    private LocalDate visaExpire;

    @NotNull
    @Schema(description = "onboardee felony status", required = true)
    private boolean felonyCheck;

    @Nullable
    @Schema(description = "onboardee felony explanation", nullable = true)
    private String felonyList;

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
        return visaExpire;
    }

    public void setVisaExpiry(@Nullable LocalDate visaExpire) {
        this.visaExpire = visaExpire;
    }

    @NotNull
    public Boolean getFelonyCheck() {
        return felonyCheck;
    }

    public void setFelonyCheck(@NotNull Boolean felonyCheck) {
        this.felonyCheck = felonyCheck;
    }

    @NotNull
    public String getFelonyList(){
        return felonyList;
    }

    public void setFelonyList(@NotNull String felonyList){
        this.felonyList = felonyList;
    }
}