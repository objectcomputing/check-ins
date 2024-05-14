package com.objectcomputing.checkins.services.employee_hours;

import io.micronaut.core.annotation.Introspected;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Introspected
public class EmployeeHoursResponseDTO {

    @NotNull
    private Long recordCountDeleted;

    @NotNull
    private Long recordCountInserted;

    private Set<EmployeeHours> employeehoursSet;
}
