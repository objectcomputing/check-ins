package com.objectcomputing.checkins.services.onboard.onboardee_about;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.model.NewHireAccountEntity;
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
import java.util.Objects;
import java.util.UUID;

import static io.micronaut.data.annotation.Relation.Kind.ONE_TO_ONE;

@Entity
@Introspected
@Table(name = "onboardee_about")
public class OnboardeeAbout {
    @Id
    @Column(name = "about_you_id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the new employee profile this entry is associated with")
    private UUID id;
    
    @NotBlank
    @Column(name = "tshirt_size")
    @ColumnTransformer(
        read = "pgp_sym_decrypt(tshirtSize::bytea,'${aes.key}')",
        write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "T-shirt size requested")
    private String tshirtSize;

    @NotBlank
    @Column(name = "google_training")
    @ColumnTransformer(
        read = "pgp_sym_decrypt(googleTraining::bytea,'${aes.key}')",
        write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "Additional training requested for Google Tools")
    private String googleTraining;

    @Nullable
    @Column(name = "introduction")
    @ColumnTransformer(
        read = "pgp_sym_decrypt(introduction::bytea,'${aes.key}')",
        write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "Introduction provided by onboardee")
    private String introduction;

    @NotBlank
    @Column(name = "vaccine_status")
    @Schema(description = "Has the onboardee taken required vaccinations")
    private Boolean vaccineStatus;

    @NotBlank
    @Column(name = "vaccine_two_weeks")
    @Schema(description = "Has is been two weeks since last vaccine dose")
    private Boolean vaccineTwoWeeks;

    @Nullable
    @Column(name = "other_training")
    @ColumnTransformer(
        read = "pgp_sym_decrypt(otherTraining::bytea,'${aes.key}')",
        write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "Additional training requested based on role")
    private String otherTraining;

    @Nullable
    @Column(name = "additional_skills")
    @ColumnTransformer(
        read = "pgp_sym_decrypt(additionalSkills::bytea,'${aes.key}')",
        write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "Additional skills that onboardee has")
    private String additionalSkills;

    @Nullable
    @Column(name = "certifications")
    @ColumnTransformer(
        read = "pgp_sym_decrypt(certifications::bytea,'${aes.key}')",
        write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "Any other certifications or training onboardee already has")
    private String certifications;

    @Relation(value = ONE_TO_ONE)
    @Column(name="new_hire_account_id")
    @JsonIgnore
    private NewHireAccountEntity newHireAccount;

    public OnboardeeAbout(UUID id, String tshirtSize, String googleTraining, @Nullable String introduction, Boolean vaccineStatus, Boolean vaccineTwoWeeks, @Nullable String otherTraining, @Nullable String additionalSkills, @Nullable String certifications, NewHireAccountEntity newHireAccount) {
        this.id = id;
        this.tshirtSize = tshirtSize;
        this.googleTraining = googleTraining;
        this.introduction = introduction;
        this.vaccineStatus = vaccineStatus;
        this.vaccineTwoWeeks = vaccineTwoWeeks;
        this.otherTraining = otherTraining;
        this.additionalSkills = additionalSkills;
        this.certifications = certifications;
        this.newHireAccount = newHireAccount;
    }

    public OnboardeeAbout(String tshirtSize, String googleTraining, @Nullable String introduction, Boolean vaccineStatus, Boolean vaccineTwoWeeks, @Nullable String otherTraining, @Nullable String additionalSkills, @Nullable String certifications, NewHireAccountEntity newHireAccount) {
        this.tshirtSize = tshirtSize;
        this.googleTraining = googleTraining;
        this.introduction = introduction;
        this.vaccineStatus = vaccineStatus;
        this.vaccineTwoWeeks = vaccineTwoWeeks;
        this.otherTraining = otherTraining;
        this.additionalSkills = additionalSkills;
        this.certifications = certifications;
        this.newHireAccount = newHireAccount;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTshirtSize() {
        return tshirtSize;
    }

    public void setTshirtSize(String tshirtSize) {
        this.tshirtSize = tshirtSize;
    }

    public String getGoogleTraining() {
        return googleTraining;
    }

    public void setGoogleTraining(String googleTraining) {
        this.googleTraining = googleTraining;
    }

    @Nullable
    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(@Nullable String introduction) {
        this.introduction = introduction;
    }

    public Boolean getVaccineStatus() {
        return vaccineStatus;
    }

    public void setVaccineStatus(Boolean vaccineStatus) {
        this.vaccineStatus = vaccineStatus;
    }

    public Boolean getVaccineTwoWeeks() {
        return vaccineTwoWeeks;
    }

    public void setVaccineTwoWeeks(Boolean vaccineTwoWeeks) {
        this.vaccineTwoWeeks = vaccineTwoWeeks;
    }

    @Nullable
    public String getOtherTraining() {
        return otherTraining;
    }

    public void setOtherTraining(@Nullable String otherTraining) {
        this.otherTraining = otherTraining;
    }

    @Nullable
    public String getAdditionalSkills() {
        return additionalSkills;
    }

    public void setAdditionalSkills(@Nullable String additionalSkills) {
        this.additionalSkills = additionalSkills;
    }

    @Nullable
    public String getCertifications() {
        return certifications;
    }

    public void setCertifications(@Nullable String certifications) {
        this.certifications = certifications;
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
        OnboardeeAbout that = (OnboardeeAbout) o;
        return Objects.equals(id, that.id) && Objects.equals(tshirtSize, that.tshirtSize) && Objects.equals(googleTraining, that.googleTraining) && Objects.equals(introduction, that.introduction) && Objects.equals(vaccineStatus, that.vaccineStatus) && Objects.equals(vaccineTwoWeeks, that.vaccineTwoWeeks) && Objects.equals(otherTraining, that.otherTraining) && Objects.equals(additionalSkills, that.additionalSkills) && Objects.equals(certifications, that.certifications) && Objects.equals(newHireAccount, that.newHireAccount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tshirtSize, googleTraining, introduction, vaccineStatus, vaccineTwoWeeks, otherTraining, additionalSkills, certifications, newHireAccount);
    }
}