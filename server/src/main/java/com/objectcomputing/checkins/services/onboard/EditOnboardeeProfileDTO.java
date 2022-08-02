package com.objectcomputing.checkins.services.onboard;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class EditOnboardeeProfileDTO {
    @NotNull
    @Schema(description = "id of the onboardee this profile entry is associated with", required = true)
    private UUID id;

    @NotBlank //the below field,firstName, is not allowed to be blank on submission
    @Schema(description = "first name of the new onboardee")
    private String firstName;

    @NotBlank
    @Schema(description = "last name of the new onboardee")
    private String lastName;

    @NotBlank
    @Schema(description = "position of the new onboardee")
    private String position;

    @NotBlank
    @Schema(description = "hire type of the new onboardee")
    private String hireType;

    @NotBlank
    @Schema(description = "email of the new onboardee")
    private String email;

    @NotBlank
    @Schema(description = "pdl of the new onboardee")
    private String pdl;

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
    public String toString() {
        return "EditOnboardeeProfileDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' + 
                ", lastName='" + lastName +'\'' +
                ", position='" + position +'\'' +
                ", hireType='" + hireType +'\'' +
                ", email='" + email + '\'' +
                ", pdl='" + pdl + '\'' + '}';
    }
}
