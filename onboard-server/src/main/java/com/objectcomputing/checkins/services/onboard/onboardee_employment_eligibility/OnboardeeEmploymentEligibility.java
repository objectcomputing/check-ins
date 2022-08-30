package com.objectcomputing.checkins.services.onboard.onboardee_employment_eligibility;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.objectcomputing.checkins.newhire.model.NewHireAccountEntity;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.Relation;
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

import static io.micronaut.data.annotation.Relation.Kind.ONE_TO_ONE;

@Entity
@Introspected
@Table(name = "onboardee_employment_eligibility")
public class OnboardeeEmploymentEligibility {
    @Id
    @Column(name = "employment_eligibility_id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the new employee profile this entry is associated with")
    private UUID id;

    @NotBlank
    @Column(name = "age_legal")
    @Schema(description = "is the new employee 18 years old or older")
    private Boolean ageLegal;

    @NotBlank
    @Column(name = "us_citizen")
    @Schema(description = "is the new employee a US citizen")
    private Boolean usCitizen;

    @Nullable
    @Column(name = "visa_status")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(visaStatus::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "new employee's visa status")
    private String visaStatus;

    @Nullable
    @Column(name = "expiration_date")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(expirationDate::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "expiration date of visa")
    private LocalDate expirationDate;

    @NotBlank
    @Column(name = "felony_status")
    @Schema(description = "has the new employee been convicted of a felony")
    private Boolean felonyStatus;

    @Nullable
    @Column(name = "felony_explanation")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(felonyExplanation::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "explanation of convicted felony")
    private String felonyExplanation;

    @Relation(value = ONE_TO_ONE)
    @Column(name="new_hire_account_id")
    @JsonIgnore
    private NewHireAccountEntity newHireAccount;

    public OnboardeeEmploymentEligibility(NewHireAccountEntity newHireAccountEntity, Boolean ageLegal,Boolean usCitizen, @Nullable String visaStatus, @Nullable LocalDate expirationDate, Boolean felonyStatus, @Nullable String felonyExplanation) {
        this.ageLegal = ageLegal;
        this.usCitizen = usCitizen;
        this.visaStatus = visaStatus;
        this.expirationDate = expirationDate;
        this.felonyStatus = felonyStatus;
        this.felonyExplanation = felonyExplanation;
        this.newHireAccount = newHireAccountEntity;
    }

    public OnboardeeEmploymentEligibility(NewHireAccountEntity newHireAccountEntity, UUID id, Boolean ageLegal, Boolean usCitizen, @Nullable String visaStatus, @Nullable LocalDate expirationDate, Boolean felonyStatus, @Nullable String felonyExplanation) {
        this.id = id;
        this.ageLegal = ageLegal;
        this.usCitizen = usCitizen;
        this.visaStatus = visaStatus;
        this.expirationDate = expirationDate;
        this.felonyStatus = felonyStatus;
        this.felonyExplanation = felonyExplanation;
        this.newHireAccount = newHireAccountEntity;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }


    public Boolean getAgeLegal() {
        return ageLegal;
    }

    public void setAgeLegal(Boolean ageLegal) {
        this.ageLegal = ageLegal;
    }

    public Boolean getUsCitizen() {
        return usCitizen;
    }

    public void setUsCitizen(Boolean usCitizen) {
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

    public Boolean getFelonyStatus() {
        return felonyStatus;
    }

    public void setFelonyStatus(Boolean felonyStatus) {
        this.felonyStatus = felonyStatus;
    }

    @Nullable
    public String getFelonyExplanation() {
        return felonyExplanation;
    }

    public void setFelonyExplanation(@Nullable String felonyExplanation) {
        this.felonyExplanation = felonyExplanation;
    }

    public NewHireAccountEntity getNewHireAccount() {
        return newHireAccount;
    }

    public void setNewHireAccount(NewHireAccountEntity newHireAccount) {
        this.newHireAccount = newHireAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OnboardeeEmploymentEligibility that = (OnboardeeEmploymentEligibility) o;
        return Objects.equals(id, that.id) && Objects.equals(ageLegal, that.ageLegal) && Objects.equals(usCitizen, that.usCitizen) && Objects.equals(visaStatus, that.visaStatus) && Objects.equals(expirationDate, that.expirationDate) && Objects.equals(felonyStatus, that.felonyStatus) && Objects.equals(felonyExplanation, that.felonyExplanation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ageLegal, usCitizen, visaStatus, expirationDate, felonyStatus, felonyExplanation);
    }
}
