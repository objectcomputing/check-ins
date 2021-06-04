package com.objectcomputing.checkins.services.employee_hours;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "employee_hours")
public class EmployeeHours {
    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "UUID of employee hours", required = true)
    private UUID id;

    @NotNull
    @Column(name="employeeId")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "employee id", required = true)
    private String employeeId;


    @NotNull
    @Column(name="contributionHours")
    @TypeDef(type = DataType.FLOAT)
    @Schema(description ="contibution hours of employee", required=true)
    private float contributionHours;

    @Column(name="billableHours")
    @TypeDef(type = DataType.FLOAT)
    @Schema(description ="billable hours of employee")
    private float billableHours;

    @Column(name="ptoHours")
    @TypeDef(type = DataType.FLOAT)
    @Schema(description ="PTO hours of employee")
    private float ptoHours;

    @Column(name="updatedDate")
    @NotNull
    @Schema(description = "date for updatedDate", required = true)
    private LocalDate updatedDate;



    public EmployeeHours(UUID id, @NotNull String employeeId, @NotNull float contributionHours, float billableHours, float ptoHours,LocalDate updatedDate) {
        this.id = id;
        this.employeeId = employeeId;
        this.contributionHours = contributionHours;
        this.billableHours = billableHours;
        this.ptoHours = ptoHours;
        this.updatedDate=updatedDate;
    }

    public EmployeeHours(@NotNull String employeeId, @NotNull float contributionHours, float billableHours, float ptoHours,LocalDate updatedDate) {
        this(null,employeeId,contributionHours,billableHours,ptoHours,updatedDate);
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public float getContributionHours() {
        return contributionHours;
    }

    public void setContributionHours(float contributionHours) {
        this.contributionHours = contributionHours;
    }

    public float getBillableHours() {
        return billableHours;
    }

    public void setBillableHours(float billableHours) {
        this.billableHours = billableHours;
    }

    public float getPtoHours() {
        return ptoHours;
    }

    public void setPtoHours(float ptoHours) {
        this.ptoHours = ptoHours;
    }

    public LocalDate getUpdatedDate() { return updatedDate; }

    public void setUpdatedDate(LocalDate updatedDate) { this.updatedDate = updatedDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeHours that = (EmployeeHours) o;
        return Float.compare(that.contributionHours, contributionHours) == 0 &&
                Float.compare(that.billableHours, billableHours) == 0 &&
                Float.compare(that.ptoHours, ptoHours) == 0 &&
                id.equals(that.id) &&
                employeeId.equals(that.employeeId) &&
                updatedDate.equals(that.updatedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, employeeId, contributionHours, billableHours, ptoHours, updatedDate);
    }

    @Override
    public String toString() {
        return "EmployeeHours{" +
                "id=" + id +
                ", employeeId='" + employeeId + '\'' +
                ", contributionHours=" + contributionHours +
                ", billableHours=" + billableHours +
                ", ptoHours=" + ptoHours +
                ", updatedDate=" + updatedDate +
                '}';
    }
}
