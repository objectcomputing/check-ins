package com.objectcomputing.checkins.services.onboard;

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


//this file contains the variables and coloum names for the edit onboardee modal component.
@Entity
@Introspected
@Table(name = "edit_onboardee_profile")
public class EditOnboardeeProfile {
    @Id // indicates this member field below is the primary key of the current entity
    @Column(name = "id") // indicates this value is stored under a column in the database with the name
    // "id"
    @AutoPopulated // Micronaut will autopopulate a user id for each onboardee's profile
    // automatically
    @TypeDef(type = DataType.STRING) // indicates what type of data will be stored in the database
    @Schema(description = "id of the new employee profile this entry is associated with", required = true)
    private UUID id;

    @NotBlank // the below field,firstName, is not allowed to be blank on submission
    @Column(name = "firstname")
    @ColumnTransformer(read = "pgp_sym_decrypt(firstName::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    // @columnTransformer and the code that follows allows the firstname field to be
    // stored as encrypted in the database and then decrypted if you want to read
    // it.
    @Schema(description = "first name of the new employee")
    private String firstName;

    @NotBlank
    @Column(name = "lastname")
    @ColumnTransformer(read = "pgp_sym_decrypt(lastName::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "last name of the new employee")
    private String lastName;

    @NotBlank
    @Column(name = "position")
    @ColumnTransformer(read = "pgp_sym_decrypt(lastName::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "The position of the new employee")
    private String position;

    @NotBlank
    @Column(name = "hiretype")
    @ColumnTransformer(read = "pgp_sym_decrypt(lastName::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "The hire type of the new employee")
    private String hireType;

    @NotBlank
    @Column(name = "email")
    @ColumnTransformer(read = "pgp_sym_decrypt(lastName::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "The email of the new employee")
    private String email;

    @NotBlank
    @Column(name = "pdl")
    @ColumnTransformer(read = "pgp_sym_decrypt(lastName::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "The pdl of the new employee")
    private String pdl;

    public EditOnboardeeProfile(String firstName, String lastName, String position, String hireType, String email,
            String pdl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.hireType = hireType;
        this.email = email;
        this.pdl = pdl;
    }

    public EditOnboardeeProfile(UUID id, String firstName, String lastName, String position, String hireType,
            String email, String pdl) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.hireType = hireType;
        this.email = email;
        this.pdl = pdl;
    }

    public EditOnboardeeProfile() {
    }

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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    public void setHireType(String hireType) {
        this.hireType = hireType;
    }

    public String getHireType() {
        return hireType;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPdl(String pdl) {
        this.pdl = pdl;
    }

    public String getPdl() {
        return pdl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EditOnboardeeProfile that = (EditOnboardeeProfile) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(position, that.position) &&
                Objects.equals(hireType, that.hireType) &&
                Objects.equals(email, that.email) &&
                Objects.equals(pdl, that.pdl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, position, hireType, email, pdl);

    }
}
