package com.objectcomputing.checkins.services.employee_hours;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Introspected
public class EmployeeHoursResponseDTO {

    @NotNull
    private Long recordCountDeleted;

    @NotNull
    private Long recordCountInserted;

    private Set<EmployeeHours> employeehoursSet;

    public Long getRecordCountDeleted() {
        return recordCountDeleted;
    }

    public void setRecordCountDeleted(Long recordCountDeleted) {
        this.recordCountDeleted = recordCountDeleted;
    }

    public Long getRecordCountInserted() {
        return recordCountInserted;
    }

    public void setRecordCountInserted(Long recordCountInserted) {
        this.recordCountInserted = recordCountInserted;
    }

    public Set<EmployeeHours> getEmployeehoursSet() {
        return employeehoursSet;
    }

    public void setEmployeehoursSet(Set<EmployeeHours> employeehoursSet) {
        this.employeehoursSet = employeehoursSet;
    }
}
