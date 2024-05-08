package com.objectcomputing.checkins.services.memberprofile;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.annotation.sql.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Introspected
@Table(name = "member_profile")
public class MemberProfile {

    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type=DataType.STRING)
    @Schema(description = "id of the member profile this entry is associated with", required = true)
    private UUID id;

    @NotBlank
    @Column(name = "firstname")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(firstName::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "first name of the employee")
    private String firstName;

    @Nullable
    @Column(name = "middlename")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(middleName::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "middle name of the employee")
    private String middleName;

    @NotBlank
    @Column(name = "lastname")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(lastName::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "last name of the employee")
    private String lastName;

    @Nullable
    @Column(name = "suffix")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(suffix::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "suffix of the employee")
    private String suffix;

    @Column(name="title")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(title::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Nullable
    @Schema(description = "employee's title at the company")
    private String title ;

    @Column(name="pdlid")
    @TypeDef(type=DataType.STRING)
    @Nullable
    @Schema(description = "employee's professional development lead")
    private UUID pdlId;

    @Column(name="location")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(location::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Nullable
    @Schema(description = "where the employee is geographically located")
    private String location;

    @NotNull
    @Column(name="workemail")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(workEmail::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "employee's OCI email. Typically last name + first initial @ObjectComputing.com", required = true)
    private String workEmail;

    @Column(name="employeeid")
    @Nullable
    @Schema(description = "unique identifier for this employee")
    private String employeeId;

    @Column(name="startdate")
    @Schema(description = "employee's date of hire")
    @Nullable
    private LocalDate startDate;

    @Column(name="biotext")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(bioText::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Nullable
    @Schema(description = "employee's biography")
    private String bioText;

    @Nullable
    @Column(name = "supervisorid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the supervisor this member is associated with", nullable = true)
    private UUID supervisorid;

    @Column(name="terminationdate")
    @Schema(description = "employee's date of termination")
    @Nullable
    private LocalDate terminationDate;

    @Column(name="birthdate")
    @Schema(description = "employee's birthdate")
    @Nullable
    private LocalDate birthDate;

    @Column(name="voluntary", columnDefinition = "boolean default false")
    @Schema(description = "termination was voluntary")
    @Nullable
    private Boolean voluntary;

    @Column(name="excluded", columnDefinition = "boolean default false")
    @Schema(description = "employee is excluded from retention reports")
    @Nullable
    private Boolean excluded;

    public MemberProfile(@NotBlank String firstName,
                         @Nullable String middleName,
                         @NotBlank String lastName,
                         @Nullable String suffix,
                         @Nullable String title,
                         @Nullable UUID pdlId,
                         @Nullable String location,
                         String workEmail,
                         @Nullable String employeeId,
                         @Nullable LocalDate startDate,
                         @Nullable String bioText,
                         @Nullable UUID supervisorid,
                         @Nullable LocalDate terminationDate,
                         @Nullable LocalDate birthDate,
                         @Nullable Boolean voluntary,
                         @Nullable Boolean excluded) {
        this(null, firstName, middleName, lastName, suffix, title, pdlId, location, workEmail,
                employeeId, startDate, bioText, supervisorid, terminationDate,birthDate, voluntary, excluded);
    }

    public MemberProfile(UUID id,
                         @NotBlank String firstName,
                         @Nullable String middleName,
                         @NotBlank String lastName,
                         @Nullable String suffix,
                         @Nullable String title,
                         @Nullable UUID pdlId,
                         @Nullable String location,
                         String workEmail,
                         @Nullable String employeeId,
                         @Nullable LocalDate startDate,
                         @Nullable String bioText,
                         @Nullable UUID supervisorid,
                         @Nullable LocalDate terminationDate,
                         @Nullable LocalDate birthDate,
                         @Nullable Boolean voluntary,
                         @Nullable Boolean excluded) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.suffix = suffix;
        this.title = title;
        this.pdlId = pdlId;
        this.location = location;
        this.workEmail = workEmail;
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.bioText = bioText;
        this.supervisorid = supervisorid;
        this.terminationDate = terminationDate;
        this.birthDate = birthDate;
        this.voluntary = voluntary;
        this.excluded = excluded;
    }

    public MemberProfile() {
    }

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

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getPdlId() {
        return pdlId;
    }

    public void setPdlId(UUID pdlId) {
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

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getBioText() {
        return bioText;
    }

    public void setBioText(String bioText) {
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

    @Nullable
    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(@Nullable LocalDate birthDate) {
        this.birthDate = birthDate;
    }

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

    @Transient
    public void clearBirthYear() {
        if (Objects.nonNull(this.birthDate)) {
            birthDate = birthDate.withYear(1900);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberProfile that = (MemberProfile) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(firstName, that.firstName) &&
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
                Objects.equals(birthDate, that.birthDate) &&
                Objects.equals(voluntary, that.voluntary) &&
                Objects.equals(excluded, that.excluded);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, middleName, lastName, suffix, title, pdlId, location,
                workEmail, employeeId, startDate, bioText, supervisorid, terminationDate,birthDate,
                voluntary, excluded);
    }

    @Override
    public String toString() {
        return "MemberProfile{" +
                "id=" + id +
                ", name='" + MemberProfileUtils.getFullName(this) + '\'' +
                ", title='" + title + '\'' +
                ", pdlId=" + pdlId +
                ", location='" + location + '\'' +
                ", workEmail='" + workEmail + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", startDate=" + startDate +
                ", bioText='" + bioText + '\'' +
                ", supervisorid=" + supervisorid +
                ", terminationDate=" + terminationDate +
                ", birthDate=" + birthDate +
                ", voluntary=" + voluntary +
                ", excluded=" + excluded +
                '}';
    }
}
