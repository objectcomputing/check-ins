package com.objectcomputing.checkins.services.onboardeeprofile;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@Introspected
public class OnboardingProfileDTO {

    @NotNull
    @Schema(description = "id of the onboardee this profile entry is associated with", required = true)
    private UUID id;

    @NotBlank //the below field,firstName, is not allowed to be blank on submission
    @Schema(description = "first name of the new onboardee")
    private String firstName;

    @NotBlank //the below field, middleName, is not allowed to be blank on submission
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
                '}';
    }
}
