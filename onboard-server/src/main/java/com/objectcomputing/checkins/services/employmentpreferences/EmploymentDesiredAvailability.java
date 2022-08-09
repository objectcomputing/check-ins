package com.objectcomputing.checkins.services.employmentpreferences;

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
import java.util.UUID;

@Entity //specifies that the class is an entity and is mapped to a database table
@Introspected //indicates a type should produce a BeanIntrospection
@Table(name="work_preference") //specifies the name of the database table to be used for mapping
//see the file path ...src/resources/db/common to create the table schema from above with the name from above
public class EmploymentDesiredAvailability {

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

    public EmploymentDesiredAvailability(String desiredPosition, LocalDate desiredStartDate, String desiredSalary, Boolean currentlyEmployed,
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
    }

    public EmploymentDesiredAvailability(UUID id, String desiredPosition, LocalDate desiredStartDate, String desiredSalary, Boolean currentlyEmployed, Boolean contactCurrentEmployer, Boolean previousEmploymentOCI, Boolean noncompeteAgreement, LocalDate noncompeteExpirationDate, String discoveredOpportunity, String referredBy, String referrerEmail, String referrerJobSite, String referralTypeOther) {
        this.id = id;
        this.desiredPosition = desiredPosition;
        this.desiredStartDate = desiredStartDate;
        this.desiredSalary = desiredSalary;
        this.currentlyEmployed = currentlyEmployed;
        this.contactCurrentEmployer = contactCurrentEmployer;
        this.previousEmploymentOCI = previousEmploymentOCI;
        this.noncompeteAgreement = noncompeteAgreement;
        this.noncompeteExpirationDate = noncompeteExpirationDate;
    }

    public EmploymentDesiredAvailability() {}

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
}
