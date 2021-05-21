package com.objectcomputing.checkins.services.employee_hours;

import io.micronaut.core.annotation.Introspected;

import java.util.Set;

@Introspected
public class EmployeeHoursResponseDTO {
    private long recordCountDeleted;
    private long recordCountInserted;
    private Set<EmployeeHours> employeehoursSet ;

    public long getRecordCountDeleted() {
        return recordCountDeleted;
    }

    public Set<EmployeeHours> getEmployeehoursSet() {
        return employeehoursSet;
    }

    public void setEmployeehoursSet(Set<EmployeeHours> employeehoursSet) {
        this.employeehoursSet = employeehoursSet;
    }

    public void setRecordCountDeleted(long recordCountDeleted) {
        this.recordCountDeleted = recordCountDeleted;
    }

    public long getRecordCountInserted() {
        return recordCountInserted;
    }

    public void setRecordCountInserted(long recordCountInserted) {
        this.recordCountInserted = recordCountInserted;
    }

}
