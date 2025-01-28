package com.objectcomputing.checkins.services.employee_hours;

import io.micronaut.http.multipart.CompletedFileUpload;

import java.util.Set;
import java.util.UUID;

public interface EmployeeHoursServices {

    EmployeeHoursResponseDTO save(CompletedFileUpload file);

    Set<EmployeeHours> findByFields(String employeeId);

}
