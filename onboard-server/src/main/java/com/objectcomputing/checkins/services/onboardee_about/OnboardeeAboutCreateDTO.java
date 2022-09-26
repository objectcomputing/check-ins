package com.objectcomputing.checkins.services.onboardee_about;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotNull;

import java.util.Objects;

@Introspected
public class OnboardeeAboutCreateDTO {

    @NotNull
    @Schema(description = "T-shirt size requested", required = true)
    private String tshirtSize;

    @NotNull
    @Schema(description = "Additional training requested for Google Tools", required = true)
    private String googleTraining;

    @Nullable
    @Schema(description = "Introduction provided by onboardee", nullable = true)
    private String introduction;

    @NotNull
    @Schema(description = "Has the onboardee taken required vaccinations", required = true)
    private Boolean vaccineStatus;

    @NotNull
    @Schema(description = "Has is been two weeks since last vaccine dose", required = true)
    private Boolean vaccineTwoWeeks;

    @Nullable
    @Schema(description = "Additional training requested based on role", nullable = true)
    private String otherTraining;

    @Nullable
    @Schema(description = "Additional skills that onboardee has", nullable = true)
    private String additionalSkills;

    @Nullable
    @Schema(description = "Any other certifications or training onboardee already has", nullable = true)
    private String certifications;

    @NotNull
    @Schema(description ="email address of the newHire used to initialize their account")
    private String emailAddress;

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

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OnboardeeAboutCreateDTO that = (OnboardeeAboutCreateDTO) o;
        return Objects.equals(tshirtSize, that.tshirtSize) && Objects.equals(googleTraining, that.googleTraining) && Objects.equals(introduction, that.introduction) && Objects.equals(vaccineStatus, that.vaccineStatus) && Objects.equals(vaccineTwoWeeks, that.vaccineTwoWeeks) && Objects.equals(otherTraining, that.otherTraining) && Objects.equals(additionalSkills, that.additionalSkills) && Objects.equals(certifications, that.certifications) && Objects.equals(emailAddress, that.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tshirtSize, googleTraining, introduction, vaccineStatus, vaccineTwoWeeks, otherTraining, additionalSkills, certifications, emailAddress);
    }
}
