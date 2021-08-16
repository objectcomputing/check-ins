package com.objectcomputing.checkins.services.memberprofile;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Introspected
public class MemberProfileCreateDTO {

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
    @Schema(description = "employee's title at the company", required = true)
    private String title ;

    @Nullable
    @Schema(description = "employee's professional development lead")
    private UUID pdlId;

    @NotBlank
    @Schema(description = "where the employee is geographically located", required = true)
    private String location;

    @NotBlank
    @Schema(description = "employee's OCI email. Typically last name + first initial @ObjectComputing.com", required = true)
    private String workEmail;

    @Nullable
    @Schema(description = "unique identifier for this employee")
    private String employeeId;

    @NotNull
    @Schema(description = "employee's date of hire", required = true)
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

    @Nullable
    @Schema(description = "Birth date of employee", nullable = true)
    private LocalDate birthDay;

    @Nullable
    @Schema(description = "The employee termination was voluntary", nullable = true)
    private Boolean voluntary;

    @Nullable
    @Schema(description = "The employee is excluded from retention reports", nullable = true)
    private Boolean excluded;

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

    public void setTerminationDate(@Nullable LocalDate terminationDate) {
        this.terminationDate = terminationDate;
    }

    @Nullable
    public LocalDate getBirthDay() { return birthDay; }

    public void setBirthDay(@Nullable LocalDate birthDay) { this.birthDay = birthDay;}

    @Nullable
    public Boolean getVoluntary() {
        return voluntary;
    }

    public void setVoluntary(@Nullable Boolean voluntary) {
        this.voluntary = voluntary;
    }

    @Nullable
    public Boolean getExcluded() {
        return excluded;
    }

    public void setExcluded(@Nullable Boolean excluded) {
        this.excluded = excluded;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberProfileCreateDTO that = (MemberProfileCreateDTO) o;
        return Objects.equals(firstName, that.firstName) &&
                Objects.equals(middleName, that.middleName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(suffix, that.suffix) &&
                Objects.equals(title, that.title) &&
                Objects.equals(pdlId, that.pdlId) &&
                Objects.equals(location, that.location) &&
                Objects.equals(workEmail, that.workEmail) &&
                Objects.equals(employeeId, that.employeeId) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(bioText, that.bioText) &&
                Objects.equals(supervisorid, that.supervisorid) &&
                Objects.equals(terminationDate, that.terminationDate) &&
                Objects.equals(birthDay, that.birthDay) &&
                Objects.equals(voluntary, that.voluntary) &&
                Objects.equals(excluded, that.excluded);

    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, middleName, lastName, suffix, title, pdlId, location,
                workEmail, employeeId, startDate, bioText, supervisorid, terminationDate, birthDay,
                voluntary, excluded);
    }
}
