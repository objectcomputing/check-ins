package com.objectcomputing.checkins.services.WorkPreference;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity //specifies that the class is an entity and is mapped to a database table
@Introspected //indicates a type should produce a BeanIntrospection
@Table(name="work_preference") //specifies the name of the database table to be used for mapping
//see the file path ...src/resources/db/common to create the table schema from above with the name from above
public class WorkPreference {

    // Begin Employment Desired and Availability
    @Id // indicates this member field below is the primary key of the current entity
    @Column(name = "id") //indicates this value is stored under a column in the database with the name "id"
    @AutoPopulated //Micronaut will autopopulate a user id for each onboardee's profile automatically
    @TypeDef(type = DataType.STRING) //indicates what type of data will be stored in the database
    @Schema(description = "id of the new employee profile this entry is associated with", required = true)
    private UUID id;

    @NotBlank
    @Column(name = "desiredposition")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(desiredposition::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    // @columnTransformer and the code that follows allows the firstname field to be stored as encrypted in the database and then decrypted if you want to read it.
    @Schema(description = "position the applicant is applying for")
    private String desiredPosition;

    @NotBlank
    @Column(name = "desiredstartdate")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(desiredstartdate::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "applicant's desired start date")
    private LocalDate desiredStartDate;

    @NotBlank
    @Column(name = "desiredsalary")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(desiredsalary::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "applicant's desired salary")
    private String desiredSalary;

    @NotBlank
    @Column(name = "currentlyemployed")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(currentlyemployed::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "is the applicant currently employed")
    private Boolean currentlyEmployed;

    @Nullable
    @Column(name = "contactcurrentemployer")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(contactcurrentemployer::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "can we contact applicant's current employer")
    private Boolean contactCurrentEmployer;

    @NotBlank
    @Column(name = "previousemploymentoci")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(previousemploymentoci::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "has the applicant worked at oci before")
    private Boolean previousEmploymentOCI;

    @NotBlank
    @Column(name = "noncompeteagreement")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(noncompeteagreement::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "what position the applicant is applying for")
    private Boolean noncompeteAgreement;

    @Nullable
    @Column(name = "noncompeteagreementexpirationdate")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(noncompeteagreementexpirationdate::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "expiration date of the non-compete agreement")
    private LocalDate noncompeteExpirationDate;

    // Begin of Referral Type

    @Nullable
    @Column(name = "discoveredopportunity")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(discoveredopportunity::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "how the applicant learned of this opportunity")
    private String discoveredOpportunity;

    @Nullable
    @Column(name = "referredby")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(referredby::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "whoever referred the applicant")
    private String referredBy;

    @Nullable
    @Column(name = "referreremail")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(referreremail::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "email of the referrer")
    private String referrerEmail;

    @Nullable
    @Column(name = "referrerjobsite")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(referrerjobsite::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "referrer's job site")
    private String referrerJobSite;

    @Nullable
    @Column(name = "referraltypeother")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(referraltypeother::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "other section in referral type")
    private String referralTypeOther;

    public WorkPreference(String desiredPosition, LocalDate desiredStartDate, String desiredSalary, Boolean currentlyEmployed,
                          @Nullable Boolean contactCurrentEmployer, Boolean previousEmploymentOCI, Boolean noncompeteAgreement, @Nullable LocalDate noncompeteExpirationDate,
                          @Nullable String discoveredOpportunity, @Nullable String referredBy, @Nullable String referrerEmail, @Nullable String referrerJobSite, @Nullable String referralTypeOther) {
        this.desiredPosition = desiredPosition;
        this.desiredStartDate = desiredStartDate;
        this.desiredSalary = desiredSalary;
        this.currentlyEmployed = currentlyEmployed;
        this.contactCurrentEmployer = contactCurrentEmployer;
        this.previousEmploymentOCI = previousEmploymentOCI;
        this.noncompeteAgreement = noncompeteAgreement;
        this.noncompeteExpirationDate = noncompeteExpirationDate;

        // Referral Type
        this.discoveredOpportunity = discoveredOpportunity;
        this.referredBy = referredBy;
        this.referrerEmail = referrerEmail;
        this.referrerJobSite = referrerJobSite;
        this.referralTypeOther = referralTypeOther;
    }

    public WorkPreference() {}

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkPreference that = (WorkPreference) o;
        return Objects.equals(desiredPosition, that.desiredPosition) &&
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
        return Objects.hash(desiredPosition, desiredStartDate, desiredSalary, currentlyEmployed, contactCurrentEmployer, previousEmploymentOCI, noncompeteAgreement, noncompeteExpirationDate, discoveredOpportunity, referredBy, referrerEmail, referrerJobSite, referralTypeOther);
    }
}