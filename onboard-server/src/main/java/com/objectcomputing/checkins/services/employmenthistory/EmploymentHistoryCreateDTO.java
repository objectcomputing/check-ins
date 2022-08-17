package com.objectcomputing.checkins.services.employmenthistory;

import java.time.LocalDate;
import java.util.Objects;

import javax.validation.constraints.NotBlank;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;

@Introspected
public class EmploymentHistoryCreateDTO {

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
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EmploymentHistoryCreateDTO that = (EmploymentHistoryCreateDTO) o;
        return Objects.equals(company, that.company) && Objects.equals(companyAddress, that.companyAddress)
                && Objects.equals(jobTitle, that.jobTitle) && Objects.equals(startDate, that.startDate)
                && Objects.equals(endDate, that.endDate) && Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(company, companyAddress, jobTitle, startDate, endDate, reason);
    }
}
