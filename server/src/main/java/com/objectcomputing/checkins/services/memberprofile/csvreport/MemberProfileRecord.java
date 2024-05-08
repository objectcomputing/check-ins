package com.objectcomputing.checkins.services.memberprofile.csvreport;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.annotation.sql.ColumnTransformer;
import io.micronaut.data.model.DataType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

@Entity
@Introspected
@Table(name = "member_profile_record")
public class MemberProfileRecord {

    @Id
    @TypeDef(type = DataType.STRING)
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

    @Column(name = "pdlfirstname")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(pdlFirstName::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Nullable
    private String pdlFirstName;

    @Column(name = "pdllastname")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(pdlLastName::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Nullable
    private String pdlLastName;

    @Column(name = "pdlemail")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(pdlEmail::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Nullable
    private String pdlEmail;

    @Column(name = "supervisorfirstname")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(supervisorFirstName::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Nullable
    private String supervisorFirstName;

    @Column(name = "supervisorlastname")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(supervisorLastName::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Nullable
    private String supervisorLastName;

    @Column(name = "supervisoremail")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(supervisorEmail::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Nullable
    private String supervisorEmail;

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

    @Transient
    public String getTenure() {
        if (startDate == null) {
            return null;
        }

        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(startDate, currentDate);
        int years = period.getYears();
        int months = period.getMonths();
        String yearsString = years == 1 ? "year" : "years";
        String monthsString = months == 1 ? "month" : "months";

        return years + " " + yearsString + " " + months + " " + monthsString;
    }

    @Nullable
    public String getPdlFirstName() {
        return pdlFirstName;
    }

    public void setPdlFirstName(@Nullable String pdlFirstName) {
        this.pdlFirstName = pdlFirstName;
    }

    @Nullable
    public String getPdlLastName() {
        return pdlLastName;
    }

    public void setPdlLastName(@Nullable String pdlLastName) {
        this.pdlLastName = pdlLastName;
    }

    @Transient
    @Nullable
    public String getPdlName() {
        if (getPdlFirstName() == null || getPdlLastName() == null) {
            return null;
        }
        return getPdlFirstName() + " " + getPdlLastName();
    }

    @Transient
    public void setPdlName(@Nullable String pdlName) {
        if (pdlName == null) {
            setPdlFirstName(null);
            setPdlLastName(null);
        } else {
            String[] names = pdlName.split(" ");
            setPdlFirstName(names[0]);
            setPdlLastName(names[1]);
        }
    }

    @Nullable
    public String getPdlEmail() {
        return pdlEmail;
    }

    public void setPdlEmail(@Nullable String pdlEmail) {
        this.pdlEmail = pdlEmail;
    }

    @Nullable
    public String getSupervisorFirstName() {
        return supervisorFirstName;
    }

    public void setSupervisorFirstName(@Nullable String supervisorFirstName) {
        this.supervisorFirstName = supervisorFirstName;
    }

    @Nullable
    public String getSupervisorLastName() {
        return supervisorLastName;
    }

    public void setSupervisorLastName(@Nullable String supervisorLastName) {
        this.supervisorLastName = supervisorLastName;
    }

    @Transient
    @Nullable
    public String getSupervisorName() {
        if (getSupervisorFirstName() == null || getSupervisorLastName() == null) {
            return null;
        }
        return getSupervisorFirstName() + " " + getSupervisorLastName();
    }

    @Transient
    public void setSupervisorName(@Nullable String supervisorName) {
        if (supervisorName == null) {
            setSupervisorFirstName(null);
            setSupervisorLastName(null);
        } else {
            String[] names = supervisorName.split(" ");
            setSupervisorFirstName(names[0]);
            setSupervisorLastName(names[1]);
        }
    }

    @Nullable
    public String getSupervisorEmail() {
        return supervisorEmail;
    }

    public void setSupervisorEmail(@Nullable String supervisorEmail) {
        this.supervisorEmail = supervisorEmail;
    }
}
