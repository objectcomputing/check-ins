package com.objectcomputing.checkins.services.onboard.onboardeeprofile;
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
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity //specifies that the class is an entity and is mapped to a database table
@Introspected //indicates a type should produce a BeanIntrospection
@Table(name="onboard_profile") //specifies the name of the database table to be used for mappe
//see the file path ...src/resources/db/common to create the table schema from above with the name from above
public class OnboardingProfile {

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

    @Nullable
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
    private String socialSecurityNumber;

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
            read = "pgp_sym_decrypt(previousAddress::bytea,'${aes.key}')",
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
    private String phoneNumber;

    @Nullable
    @Column(name = "secondphonenumber")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(secondPhoneNumber::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = " 2nd phone # of the new employee")
    private String secondPhoneNumber;

    @NotNull
    @Column(name="personalemail")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(personalEmail::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "onboardee's personal email.", required = true)
    private String personalEmail;

    @NotNull
    @Column(name="backgroundid")
    @TypeDef(type=DataType.STRING)
    @Schema(description = "id of background information", required = true)
    private UUID backgroundId;


    public OnboardingProfile(String firstName, @Nullable String middleName, String lastName, String socialSecurityNumber, LocalDate birthDate, String currentAddress, @Nullable String previousAddress, String phoneNumber, @Nullable String secondPhoneNumber, String personalEmail, UUID backgroundId) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.socialSecurityNumber = socialSecurityNumber;
        this.birthDate = birthDate;
        this.currentAddress = currentAddress;
        this.previousAddress = previousAddress;
        this.phoneNumber = phoneNumber;
        this.secondPhoneNumber = secondPhoneNumber;
        this.personalEmail = personalEmail;
        this.backgroundId = backgroundId;
    }
    public OnboardingProfile(UUID id, String firstName, @Nullable String middleName, String lastName, String socialSecurityNumber, LocalDate birthDate, String currentAddress, @Nullable String previousAddress, String phoneNumber,@Nullable String secondPhoneNumber, String personalEmail, UUID backgroundId) {
        this.id= id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.socialSecurityNumber = socialSecurityNumber;
        this.birthDate = birthDate;
        this.currentAddress = currentAddress;
        this.previousAddress = previousAddress;
        this.phoneNumber = phoneNumber;
        this.secondPhoneNumber = secondPhoneNumber;
        this.personalEmail= personalEmail;
        this.backgroundId = backgroundId;
    }

    public OnboardingProfile(){}

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Nullable
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(@Nullable String middleName) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OnboardingProfile that = (OnboardingProfile) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(middleName, that.middleName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(socialSecurityNumber, that.socialSecurityNumber) &&
                Objects.equals(birthDate, that.birthDate) &&
                Objects.equals(currentAddress, that.currentAddress) &&
                Objects.equals(previousAddress, that.previousAddress) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(secondPhoneNumber, that.secondPhoneNumber) &&
                Objects.equals(personalEmail, that.personalEmail) &&
                Objects.equals(backgroundId, that.backgroundId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, middleName, lastName, socialSecurityNumber, birthDate, currentAddress, previousAddress, phoneNumber, secondPhoneNumber, personalEmail, backgroundId);
    }
}


