package com.objectcomputing.checkins.services.employee_hours;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/employee/hours")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name="employee hours")
public class EmployeeHoursController {
    private final EmployeeHoursServices employeeHoursServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public EmployeeHoursController(EmployeeHoursServices employeeHoursServices,
                                   EventLoopGroup eventLoopGroup,
                                   @Named(TaskExecutors.IO)ExecutorService ioExecutorService) {
        this.employeeHoursServices = employeeHoursServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     *
     * @param employeeId
     * @return
     */
    @Get("/{?employeeId}")
    public Mono<HttpResponse<Set<EmployeeHours>>> findEmployeeHours(@Nullable String employeeId) {
        return Mono.fromCallable(() -> employeeHoursServices.findByFields(employeeId))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(createdEmployeeHours -> (HttpResponse<Set<EmployeeHours>>) HttpResponse.ok(createdEmployeeHours))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }


    /**
     * @param id
     * @return
     */
    @Get("/{id}")
    public Mono<HttpResponse<EmployeeHours>> readEmployeeHours(@NotNull UUID id) {
        return Mono.fromCallable(() -> {
            EmployeeHours result = employeeHoursServices.read(id);
            if (result == null) {
                throw new NotFoundException("No employee hours for employee id");
            }
            return result;
        })
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(employeeHour -> {
                    return (HttpResponse<EmployeeHours>) HttpResponse.ok(employeeHour);
                }).subscribeOn(Schedulers.fromExecutor(ioExecutorService));

    }

    /**
     * Parse the CSV file and store it to employee hours table
     * @param file
     * @{@link HttpResponse<EmployeeHoursResponseDTO>}
     */
    @Post(uri="/upload" , consumes = MediaType.MULTIPART_FORM_DATA)
    public Mono<HttpResponse<EmployeeHoursResponseDTO>> upload(CompletedFileUpload file){
        return Mono.fromCallable(() -> employeeHoursServices.save(file))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(fileInfo -> (HttpResponse<EmployeeHoursResponseDTO>) HttpResponse.ok(fileInfo))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

}
