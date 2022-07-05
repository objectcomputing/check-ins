package com.objectcomputing.checkins.services.onboardeeprofile;
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
@Table(name="onboard_profile") //specifies the name of the database table to be used for mappe
//see the file path ...src/resources/db/common to create the table schema from above with the name from above
public class Onboarding_profile {

    @Id // indicates this member field below is the primary key of the current entity
    @Column(name = "id") //indicates this value is stored under a column in the database with the name "id"
    @AutoPopulated //Micronaut will autopopulate a user id for each onboardee's profile automatically
    @TypeDef(type = DataType.STRING) //indicates what type of data will be stored in the database
    @Schema(description = "id of the new employee profile this entry is associated with", required = true)
    private UUID id;

    @NotBlank //the below field,firstName, is not allowed to be blank on submission
    @Column(name = "firstname")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(firstName::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    // @columnTransformer and the code that follows allows the firstname field to be stored as encrypted in the database and then decrypted if you want to read it.
    @Schema(description = "first name of the new employee")
    private String firstName;

    @NotBlank //the below field, middleName, is not allowed to be blank on submission
    @Column(name = "middlename")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(middleName::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "middle name of the new employee")
    private String middleName;

    @NotBlank
    @Column(name = "lastname")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(lastName::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "last name of the new employee")
    private String lastName;

    @NotBlank
    @Column(name = "socialsecuritynumber")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(socialSecurityNumber::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "social Security # of the new employee")
    private Integer socialSecurityNumber;

    @NotBlank
    @Column(name = "birthdate")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(birthdate::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "birthdate of the new employee")
    private LocalDate birthDate;

    @NotBlank
    @Column(name = "currentaddress")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(currentAddress::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "currentAddress of the new employee")
    private String currentAddress;

    @Nullable
    @Column(name = "previousaddress")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(reviousAddress::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "previousAddress of the new employee")
    private String previousAddress;

    @NotBlank
    @Column(name = "phonenumber")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(phoneNumber::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "phone # of the new employee")
    private Integer phoneNumber;

    @Nullable
    @Column(name = "secondphonenumber")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(secondPhoneNumber::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = " 2nd phone # of the new employee")
    private Integer secondPhoneNumber;

    public Onboarding_profile(UUID id, String firstName, String middleName, String lastName, Integer socialSecurityNumber, Date birthDate, String currentAddress, @Nullable String previousAddress, Integer phoneNumber) {
        this.id= id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.socialSecurityNumber = socialSecurityNumber;
        this.birthDate = birthDate;
        this.currentAddress = currentAddress;
        this.previousAddress = previousAddress;
        this.phoneNumber = phoneNumber;
    }

    public Onboarding_profile (){}

    public UUID getId() {
        return id;
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

    public Integer getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(Integer socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
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
}


