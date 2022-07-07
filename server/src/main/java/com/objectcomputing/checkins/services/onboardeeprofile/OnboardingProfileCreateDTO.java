package com.objectcomputing.checkins.services.onboardeeprofile;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Introspected
public class OnboardingProfileCreateDTO {

    @NotBlank //the below field,firstName, is not allowed to be blank on submission
    @Schema(description = "first name of the new employee")
    private String firstName;

    @NotBlank //the below field, middleName, is not allowed to be blank on submission
    @Schema(description = "middle name of the new employee")
    private String middleName;

    @NotBlank
    @Schema(description = "last name of the new employee")
    private String lastName;

    @NotBlank
    @Schema(description = "social Security # of the new employee")
    private Integer socialSecurityNumber;

    @NotBlank
    @Schema(description = "birthdate of the new employee")
    private LocalDate birthDate;

    @NotBlank
    @Schema(description = "currentAddress of the new employee")
    private String currentAddress;

    @Nullable
    @Schema(description = "previousAddress of the new employee")
    private String previousAddress;

    @NotBlank
    @Schema(description = "phone # of the new employee")
    private Integer phoneNumber;

    @Nullable
    @Schema(description = "2nd phone # of the new employee")
    private Integer secondPhoneNumber;

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

    public Integer getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(Integer socialSecurityNumber) {
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

    public Integer getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Integer phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Nullable
    public Integer getSecondPhoneNumber() {
        return secondPhoneNumber;
    }

    public void setSecondPhoneNumber(@Nullable Integer secondPhoneNumber) {
        this.secondPhoneNumber = secondPhoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OnboardingProfileCreateDTO that = (OnboardingProfileCreateDTO) o;
        return Objects.equals(firstName, that.firstName) &&
                Objects.equals(middleName, that.middleName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(socialSecurityNumber, that.socialSecurityNumber) &&
                Objects.equals(birthDate, that.birthDate) &&
                Objects.equals(currentAddress, that.currentAddress) &&
                Objects.equals(previousAddress, that.previousAddress) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(secondPhoneNumber, that.secondPhoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, middleName, lastName, socialSecurityNumber, birthDate, currentAddress, previousAddress, phoneNumber, secondPhoneNumber);
    }
}
