package com.objectcomputing.checkins.services.employmentpreferences;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Introspected
public class EmploymentDesiredAvailabilityCreateDTO {

    @NotBlank
    @Schema(description = "position the applicant is applying for")
    private String desiredPosition;

    @NotBlank
    @Schema(description = "applicant's desired start date")
    private LocalDate desiredStartDate;

    @NotBlank
    @Schema(description = "applicant's desired salary")
    private String desiredSalary;

    @NotBlank
    @Schema(description = "is the applicant currently employed")
    private Boolean currentlyEmployed;

    @Nullable
    @Schema(description = "can we contact the applicant's current employer")
    private Boolean contactCurrentEmployer;

    @NotBlank
    @Schema(description = "has the applicant worked at oci before")
    private Boolean previousEmploymentOCI;

    @NotBlank
    @Schema(description = "what position the applicant is applying for")
    private Boolean noncompeteAgreement;

    @Nullable
    @Schema(description = "expiration date of the non-compete agreement")
    private LocalDate noncompeteExpirationDate;

    public void setDesiredPosition(String desiredPosition) { this.desiredPosition = desiredPosition; }

    public String getDesiredPosition() { return desiredPosition; }

    public void setDesiredStartDate(LocalDate desiredStartDate) { this.desiredStartDate = desiredStartDate; }

    public LocalDate getDesiredStartDate() { return desiredStartDate; }

    public void setDesiredSalary(String desiredSalary) { this.desiredSalary = desiredSalary; }

    public String getDesiredSalary() { return desiredSalary; }

    public void setCurrentlyEmployed(Boolean currentlyEmployed) { this.currentlyEmployed = currentlyEmployed; }

    public Boolean getCurrentlyEmployed() { return currentlyEmployed; }

    public void setContactCurrentEmployer(Boolean contactCurrentEmployer) { this.contactCurrentEmployer = contactCurrentEmployer; }

    @Nullable
    public Boolean getContactCurrentEmployer() { return contactCurrentEmployer; }

    public void setPreviousEmploymentOCI(Boolean previousEmploymentOCI) { this.previousEmploymentOCI = previousEmploymentOCI; }

    public Boolean getPreviousEmploymentOCI() { return previousEmploymentOCI; }

    public void setNoncompeteAgreement(Boolean noncompeteAgreement) { this.noncompeteAgreement = noncompeteAgreement; }

    public Boolean getNoncompeteAgreement() { return noncompeteAgreement; }

    public void setNoncompeteExpirationDate(LocalDate noncompeteExpirationDate) { this.noncompeteExpirationDate = noncompeteExpirationDate; }

    @Nullable
    public LocalDate getNoncompeteExpirationDate() { return noncompeteExpirationDate; }

}
