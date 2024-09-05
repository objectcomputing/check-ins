package com.objectcomputing.checkins.services.employee_hours;

import com.objectcomputing.checkins.converter.LocalDateConverter;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Introspected
@Table(name = "employee_hours")
public class EmployeeHours {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "UUID of employee hours")
    private UUID id;

    @NotNull
    @Column(name="employeeid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "employee id")
    private String employeeId;

    @NotNull
    @Column(name="contributionhours")
    @TypeDef(type = DataType.FLOAT)
    @Schema(description ="contribution hours of employee")
    private Float contributionHours;

    @NotNull
    @Column(name="billablehours")
    @TypeDef(type = DataType.FLOAT)
    @Schema(description ="billable hours of employee")
    private Float billableHours;

    @NotNull
    @Column(name="ptohours")
    @TypeDef(type = DataType.FLOAT)
    @Schema(description ="PTO hours of employee")
    private Float ptoHours;

    @Column(name="updateddate")
    @NotNull
    @Schema(description = "date for updatedDate")
    @TypeDef(type = DataType.DATE, converter = LocalDateConverter.class)
    private LocalDate updatedDate;

    @Column(name="targethours")
    @NotNull
    @Schema(description = "Target hours for an employee")
    private Float targetHours;

    @Column(name="asofdate")
    @Schema(description = "as of Date")
    @TypeDef(type = DataType.DATE, converter = LocalDateConverter.class)
    private LocalDate asOfDate;

    @Column(name="billable_utilization")
    @TypeDef(type = DataType.FLOAT)
    @Schema(description ="Billable utilization hours")
    @Nullable
    private Float billableUtilization;

    @Column(name="overtime_worked")
    @TypeDef(type = DataType.FLOAT)
    @Schema(description ="Number of hours of overtime worked")
    @Nullable
    private Float overtimeWorked;

    public EmployeeHours(UUID id, @NotNull String employeeId, @NotNull Float contributionHours, Float billableHours, Float ptoHours, LocalDate updatedDate, Float targetHours, LocalDate asOfDate, @Nullable Float billableUtilization, @Nullable Float overtimeWorked) {
        this.id = id;
        this.employeeId = employeeId;
        this.contributionHours = contributionHours;
        this.billableHours = billableHours;
        this.ptoHours = ptoHours;
        this.updatedDate = updatedDate;
        this.targetHours = targetHours;
        this.asOfDate = asOfDate;
        this.billableUtilization = billableUtilization;
        this.overtimeWorked = overtimeWorked;
    }

    public EmployeeHours(@NotNull String employeeId, @NotNull Float contributionHours, @NotNull Float billableHours, @NotNull Float ptoHours, LocalDate updatedDate, @NotNull Float targetHours, LocalDate asOfDate, @Nullable Float billableUtilization, @Nullable Float overtimeWorked) {
        this(null, employeeId, contributionHours, billableHours, ptoHours, updatedDate, targetHours, asOfDate, billableUtilization, overtimeWorked);
    }

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
                updatedDate.equals(that.updatedDate) &&
                Float.compare(that.targetHours, targetHours) == 0 &&
                asOfDate.equals(that.asOfDate) &&
                Float.compare(that.billableUtilization, this.billableUtilization) == 0 &&
                Float.compare(that.overtimeWorked, this.overtimeWorked) == 0
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, employeeId, contributionHours, billableHours, ptoHours, updatedDate,targetHours, asOfDate, billableUtilization, overtimeWorked);
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
                ", targetHours=" + targetHours +
                ", asOfDate=" + asOfDate +
                ", billableUtilization=" + this.billableUtilization +
                ", overtimeWorked=" + this.overtimeWorked +
                '}';
    }
}
