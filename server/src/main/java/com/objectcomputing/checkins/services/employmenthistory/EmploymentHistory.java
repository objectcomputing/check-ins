package com.objectcomputing.checkins.services.employmenthistory;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;

@Entity
@Introspected
@Table(name = "employment_history")
public class EmploymentHistory {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "private key id")
    private UUID id;

    @NotBlank
    @Column(name = "company")
    @ColumnTransformer(read = "pgp_sym_decrypt(company::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "name of previous employer")
    private String company;

    @NotBlank
    @Column(name = "companyaddress")
    @ColumnTransformer(read = "pgp_sym_decrypt(companyAddress::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "address of previous employer")
    private String companyAddress;

    @NotBlank
    @Column(name = "jobtitle")
    @ColumnTransformer(read = "pgp_sym_decrypt(jobTitle::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "job title")
    private String jobTitle;

    @NotBlank
    @Column(name = "startdate")
    @ColumnTransformer(read = "pgp_sym_decrypt(startDate::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "start date of job")
    private LocalDate startDate;

    @NotBlank
    @Column(name = "enddate")
    @ColumnTransformer(read = "pgp_sym_decrypt(endDate::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "end date of job")
    private LocalDate endDate;

    @Nullable
    @Column(name = "reason")
    @ColumnTransformer(read = "pgp_sym_decrypt(reason::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "reason for leaving")
    private String reason;

    public EmploymentHistory(UUID id, String company, String companyAddress, String jobTitle, LocalDate startDate, LocalDate endDate, @Nullable String reason) {
        this.id = id;
        this.company = company;
        this.companyAddress = companyAddress;
        this.jobTitle = jobTitle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
    }

    public EmploymentHistory(String company, String companyAddress, String jobTitle, LocalDate startDate, LocalDate endDate, @Nullable String reason) {
        this.company = company;
        this.companyAddress = companyAddress;
        this.jobTitle = jobTitle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
    }

    public EmploymentHistory(){}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    @Nullable
    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(@Nullable String jobTitle) {
        this.jobTitle = jobTitle;
    }

    @Nullable
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(@Nullable LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Nullable
    public String getReason() {
        return reason;
    }

    public void setReason(@Nullable String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmploymentHistory that = (EmploymentHistory) o;
        return Objects.equals(id, that.id) && Objects.equals(company, that.company) && Objects.equals(companyAddress, that.companyAddress) && Objects.equals(jobTitle, that.jobTitle) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, company, companyAddress, jobTitle, startDate, endDate, reason);
    }
}
