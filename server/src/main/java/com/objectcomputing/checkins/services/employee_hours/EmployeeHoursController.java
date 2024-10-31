package com.objectcomputing.checkins.services.employee_hours;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

@Controller("/services/employee/hours")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name="employee hours")
public class EmployeeHoursController {
    private final EmployeeHoursServices employeeHoursServices;

    public EmployeeHoursController(EmployeeHoursServices employeeHoursServices) {
        this.employeeHoursServices = employeeHoursServices;
    }

    /**
     *
     * @param employeeId
     * @return
     */
    @Get("/{?employeeId}")
    public Set<EmployeeHours> findEmployeeHours(@Nullable String employeeId) {
        return employeeHoursServices.findByFields(employeeId);
    }


    /**
     * @param id
     * @return
     */
    @Get("/{id}")
    public EmployeeHours readEmployeeHours(@NotNull UUID id) {
        EmployeeHours result = employeeHoursServices.read(id);
        if (result == null) {
            throw new NotFoundException("No employee hours for employee id");
        }
        return result;
    }

    /**
     * Parse the CSV file and store it to employee hours table
     * @param file
     * @{@link HttpResponse<EmployeeHoursResponseDTO>}
     */
    @Post(uri="/upload" , consumes = MediaType.MULTIPART_FORM_DATA)
    public EmployeeHoursResponseDTO upload(CompletedFileUpload file){
        return employeeHoursServices.save(file);
    }
}
