package com.objectcomputing.checkins.services.WorkPreference;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Introspected
public class WorkPreferenceResponseDTO {

    @NotNull
    @Schema(description = "id of the new employee profile this entry is associated with", required = true)
    private UUID id;

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
    @Schema(description = "can we contact applicant's current employer")
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

    @Nullable
    @Schema(description = "how the applicant learned of this opportunity")
    private String discoveredOpportunity;

    @Nullable
    @Schema(description = "whoever referred the applicant")
    private String referredBy;

    @Nullable
    @Schema(description = "email of the referrer")
    private String referrerEmail;

    @Nullable
    @Schema(description = "referrer's job site")
    private String referrerJobSite;

    @Nullable
    @Schema(description = "other section in referral type")
    private String referralTypeOther;

    public void setId(UUID id) {
        this.id = id;
    }
    public UUID getId() {
        return id;
    }

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

    // Referral Type Begins
    public void setDiscoveredOpportunity(String discoveredOpportunity) { this.discoveredOpportunity = discoveredOpportunity; }

    @Nullable
    public String getDiscoveredOpportunity() { return discoveredOpportunity; }

    public void setReferredBy(String referredBy) { this.referredBy = referredBy; }

    @Nullable
    public String getReferredBy() { return referredBy; }

    public void setReferrerEmail(String referrerEmail) { this.referrerEmail = referrerEmail; }

    @Nullable
    public String getReferrerEmail() { return referrerEmail; }

    public void setReferrerJobSite(String referrerJobSite) { this.referrerJobSite = referrerJobSite; }

    @Nullable
    public String getReferrerJobSite() { return referrerJobSite; }

    public void setReferralTypeOther(String referralTypeOther) { this.referralTypeOther = referralTypeOther; }

    @Nullable
    public String getReferralTypeOther() { return referralTypeOther; }

    @Override
    public String toString() {
        return "WorkPreferenceResponseDTO{" +
                "id=" + id +
                ", desiredPosition='" + desiredPosition + '\'' +
                ", desiredStartDate=" + desiredStartDate +
                ", desiredSalary='" + desiredSalary + '\'' +
                ", currentlyEmployed=" + currentlyEmployed +
                ", contactCurrentEmployer=" + contactCurrentEmployer +
                ", previousEmploymentOCI=" + previousEmploymentOCI +
                ", noncompeteAgreement=" + noncompeteAgreement +
                ", noncompeteExpirationDate=" + noncompeteExpirationDate +
                ", discoveredOpportunity='" + discoveredOpportunity + '\'' +
                ", referredBy='" + referredBy + '\'' +
                ", referrerEmail='" + referrerEmail + '\'' +
                ", referrerJobSite='" + referrerJobSite + '\'' +
                ", referralTypeOther='" + referralTypeOther + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkPreferenceResponseDTO that = (WorkPreferenceResponseDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(desiredPosition, that.desiredPosition) &&
                Objects.equals(desiredStartDate, that.desiredStartDate) &&
                Objects.equals(desiredSalary, that.desiredSalary) &&
                Objects.equals(currentlyEmployed, that.currentlyEmployed) &&
                Objects.equals(contactCurrentEmployer, that.contactCurrentEmployer) &&
                Objects.equals(previousEmploymentOCI, that.previousEmploymentOCI) &&
                Objects.equals(noncompeteAgreement, that.noncompeteAgreement) &&
                Objects.equals(noncompeteExpirationDate, that.noncompeteExpirationDate) &&
                Objects.equals(discoveredOpportunity, that.discoveredOpportunity) &&
                Objects.equals(referredBy, that.referredBy) &&
                Objects.equals(referrerEmail, that.referrerEmail) &&
                Objects.equals(referrerJobSite, that.referrerJobSite) &&
    Objects.equals(referralTypeOther, that.referralTypeOther);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, desiredPosition, desiredStartDate, desiredSalary, currentlyEmployed, contactCurrentEmployer, previousEmploymentOCI, noncompeteAgreement, noncompeteExpirationDate, discoveredOpportunity, referredBy, referrerEmail, referrerJobSite, referralTypeOther);
    }
}
