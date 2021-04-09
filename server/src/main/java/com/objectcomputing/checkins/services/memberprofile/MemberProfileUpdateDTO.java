package com.objectcomputing.checkins.services.memberprofile;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@Introspected
public class MemberProfileUpdateDTO {

    @NotNull
    @Schema(description = "id of the member profile this entry is associated with", required = true)
    private UUID id;

    @NotBlank
    @Schema(description = "first name of the employee")
    private String firstName;

    @Nullable
    @Schema(description = "middle name of the employee")
    private String middleName;

    @NotBlank
    @Schema(description = "last name of the employee")
    private String lastName;

    @Nullable
    @Schema(description = "suffix of the employee")
    private String suffix;

    @NotBlank
    @Schema(description = "full name of the employee")
    private String name;

    @Nullable
    @Schema(description = "employee's title at the company")
    private String title ;

    @Nullable
    @Schema(description = "employee's professional development lead")
    private UUID pdlId;

    @Nullable
    @Schema(description = "where the employee is geographically located")
    private String location;

    @NotBlank
    @Schema(description = "employee's OCI email. Typically last name + first initial @ObjectComputing.com", required = true)
    private String workEmail;

    @Nullable
    @Schema(description = "unique identifier for this employee")
    private String employeeId;

    @Nullable
    @Schema(description = "employee's date of hire")
    private LocalDate startDate;

    @Nullable
    @Schema(description = "employee's biography")
    private String bioText;

    @Nullable
    @Schema(description = "id of the supervisor this member is associated with", nullable = true)
    private UUID supervisorid;

    @Nullable
    @Schema(description = "employee's date of termination", nullable = true)
    private LocalDate terminationDate;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @NotBlank
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotBlank String firstName) {
        this.firstName = firstName;
    }

    @Nullable
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(@Nullable String middleName) {
        this.middleName = middleName;
    }

    @NotBlank
    public String getLastName() {
        return lastName;
    }

    public void setLastName(@NotBlank String lastName) {
        this.lastName = lastName;
    }

    @Nullable
    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(@Nullable String suffix) {
        this.suffix = suffix;
    }

    @NotBlank
    public String getName() {
        return name;
    }

    public void setName(@NotBlank String name) {
        this.name = name;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    public UUID getPdlId() {
        return pdlId;
    }

    public void setPdlId(@Nullable UUID pdlId) {
        this.pdlId = pdlId;
    }

    @Nullable
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWorkEmail() {
        return workEmail;
    }

    public void setWorkEmail(String workEmail) {
        this.workEmail = workEmail;
    }

    @Nullable
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(@Nullable String employeeId) {
        this.employeeId = employeeId;
    }

    @Nullable
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Nullable
    public String getBioText() {
        return bioText;
    }

    public void setBioText(@Nullable String bioText) {
        this.bioText = bioText;
    }

    @Nullable
    public UUID getSupervisorid() {
        return supervisorid;
    }

    public void setSupervisorid(@Nullable UUID supervisorid) {
        this.supervisorid = supervisorid;
    }

    @Nullable
    public LocalDate getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(LocalDate terminationDate) {
        this.terminationDate = terminationDate;
    }
}
