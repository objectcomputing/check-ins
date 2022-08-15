package com.objectcomputing.checkins.services.onboardee_about;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class OnboardeeAboutResponseDTO {

    @NotNull
    @Schema(description = "id of the onboardee this profile is associated with", required = true)
    private UUID id;

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

    @Override
    public String toString() {
        return "OnboardeeAboutResponseDTO{" +
                "id=" + id +
                ", tshirtSize='" + tshirtSize + '\'' +
                ", googleTraining='" + googleTraining + '\'' +
                ", introduction='" + introduction + '\'' +
                ", vaccineStatus=" + vaccineStatus +
                ", vaccineTwoWeeks=" + vaccineTwoWeeks +
                ", otherTraining='" + otherTraining + '\'' +
                ", additionalSkills='" + additionalSkills + '\'' +
                ", certifications='" + certifications + '\'' +
                '}';
    }
}
