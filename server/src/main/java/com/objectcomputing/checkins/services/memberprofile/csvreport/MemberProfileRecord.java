package com.objectcomputing.checkins.services.memberprofile.csvreport;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Introspected
@Table(name = "member_profile_record")
public class MemberProfileRecord {

    @Id
    private UUID id;

    @Column(name = "firstname")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(firstName::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    private String firstName;

    @Column(name = "lastname")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(lastName::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    private String lastName;

    @Column(name = "title")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(title::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    private String title;

    @Column(name = "location")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(location::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    private String location;

    @Column(name = "workemail")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(workEmail::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    private String workEmail;

    @Column(name = "startdate")
    private LocalDate startDate;

    @Column(name = "tenure")
    private String tenure;

    @Column(name = "pdlname")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(pdlName::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Nullable
    private String pdlName;

    @Column(name = "pdlemail")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(pdlEmail::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Nullable
    private String pdlEmail;

    @Column(name = "supervisorname")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(supervisorName::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Nullable
    private String supervisorName;

    @Column(name = "supervisoremail")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(supervisorEmail::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Nullable
    private String supervisorEmail;

    public MemberProfileRecord(String firstName, String lastName, String title, String location,
                               String workEmail, LocalDate startDate, String tenure, @Nullable String pdlName,
                               @Nullable String pdlEmail, @Nullable String supervisorName,
                               @Nullable String supervisorEmail) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.location = location;
        this.workEmail = workEmail;
        this.startDate = startDate;
        this.tenure = tenure;
        this.pdlName = pdlName;
        this.pdlEmail = pdlEmail;
        this.supervisorName = supervisorName;
        this.supervisorEmail = supervisorEmail;
    }

    public MemberProfileRecord() {}

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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getTenure() {
        return tenure;
    }

    public void setTenure(String tenure) {
        this.tenure = tenure;
    }

    @Nullable
    public String getPdlName() {
        return pdlName;
    }

    public void setPdlName(@Nullable String pdlName) {
        this.pdlName = pdlName;
    }

    @Nullable
    public String getPdlEmail() {
        return pdlEmail;
    }

    public void setPdlEmail(@Nullable String pdlEmail) {
        this.pdlEmail = pdlEmail;
    }

    @Nullable
    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(@Nullable String supervisorName) {
        this.supervisorName = supervisorName;
    }

    @Nullable
    public String getSupervisorEmail() {
        return supervisorEmail;
    }

    public void setSupervisorEmail(@Nullable String supervisorEmail) {
        this.supervisorEmail = supervisorEmail;
    }
}
