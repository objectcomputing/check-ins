package com.objectcomputing.checkins.services.onboard.onboardeeprofile;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Introspected
public class OnboardingProfileDTO {

    @NotNull
    @Schema(description = "id of the onboardee this profile entry is associated with", required = true)
    private UUID id;

    @NotBlank //the below field,firstName, is not allowed to be blank on submission
    @Schema(description = "first name of the new onboardee")
    private String firstName;

    @Nullable
    @Schema(description = "middle name of the new onboardee")
    private String middleName;

    @NotBlank
    @Schema(description = "last name of the new onboardee")
    private String lastName;

    @NotBlank
    @Schema(description = "social Security # of the new onboardee")
    private String socialSecurityNumber;

    @NotBlank
    @Schema(description = "birthdate of the new onboardee")
    private LocalDate birthDate;

    @NotBlank
    @Schema(description = "currentAddress of the new onboardee")
    private String currentAddress;

    @Nullable
    @Schema(description = "previousAddress of the new onboardee")
    private String previousAddress;

    @NotBlank
    @Schema(description = "phone # of the new onboardee")
    private String phoneNumber;

    @Nullable
    @Schema(description = "2nd phone # of the new onboardee")
    private String secondPhoneNumber;

    @NotBlank
    @Schema(description = "Personal email of onboardee")
    private String personalEmail;

    @NotNull
    @Schema(description = "Background Id of onboardee")
    private UUID backgroundId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    @Nullable
    public String getPreviousAddress() {
        return previousAddress;
    }

    public void setPreviousAddress(@Nullable String previousAddress) {
        this.previousAddress = previousAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Nullable
    public String getSecondPhoneNumber() {
        return secondPhoneNumber;
    }

    public void setSecondPhoneNumber(@Nullable String secondPhoneNumber) {
        this.secondPhoneNumber = secondPhoneNumber;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public UUID getBackgroundId() { return backgroundId; }

    public void setBackgroundId(UUID backgroundId) { this.backgroundId = backgroundId; }
    @Override
    public String toString() {
        return "OnboardingProfileDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", socialSecurityNumber='" + socialSecurityNumber + '\'' +
                ", birthDate='" + birthDate +
                ", currentAddress='" + currentAddress + '\'' +
                ", previousAddress=" + previousAddress +  '\'' +
                ", phoneNumber='" + phoneNumber +
                ", secondPhoneNumber='" + secondPhoneNumber +
                ", personalEmail=" + personalEmail +  '\'' +
                ", backgroundId=" + backgroundId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OnboardingProfileDTO that = (OnboardingProfileDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(firstName, that.firstName) && Objects.equals(middleName, that.middleName) && Objects.equals(lastName, that.lastName) && Objects.equals(socialSecurityNumber, that.socialSecurityNumber) && Objects.equals(birthDate, that.birthDate) && Objects.equals(currentAddress, that.currentAddress) && Objects.equals(previousAddress, that.previousAddress) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(secondPhoneNumber, that.secondPhoneNumber) && Objects.equals(personalEmail, that.personalEmail) && Objects.equals(backgroundId, that.backgroundId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, middleName, lastName, socialSecurityNumber, birthDate, currentAddress, previousAddress, phoneNumber, secondPhoneNumber, personalEmail, backgroundId);
    }
}
