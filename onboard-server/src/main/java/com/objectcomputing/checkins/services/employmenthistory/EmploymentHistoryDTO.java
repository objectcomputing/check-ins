package com.objectcomputing.checkins.services.employmenthistory;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;

@Introspected
public class EmploymentHistoryDTO {

    @NotNull
    @Schema(description = "private key id")
    private UUID id;

    @NotBlank
    @Schema(description = "name of previous employer")
    private String company;

    @NotBlank
    @Schema(description = "address of previous employer")
    private String companyAddress;

    @NotBlank
    @Schema(description = "job title")
    private String jobTitle;

    @NotBlank
    @Schema(description = "start date of job")
    private LocalDate startDate;

    @NotBlank
    @Schema(description = "end date of job")
    private LocalDate endDate;

    @Nullable
    @Schema(description = "reason for leaving")
    private String reason;

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

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
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
    public String toString() {
        return "EmploymentHistoryDTO{" +
                "id=" + id +
                ", company='" + company + '\'' +
                ", companyAddress='" + companyAddress + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", reason='" + reason + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmploymentHistoryDTO that = (EmploymentHistoryDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(company, that.company) && Objects.equals(companyAddress, that.companyAddress) && Objects.equals(jobTitle, that.jobTitle) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, company, companyAddress, jobTitle, startDate, endDate, reason);
    }
}
