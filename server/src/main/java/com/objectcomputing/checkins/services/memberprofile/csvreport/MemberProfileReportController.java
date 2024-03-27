package com.objectcomputing.checkins.services.memberprofile.csvreport;

import com.objectcomputing.checkins.security.permissions.Permissions;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Named;

import java.io.*;
import java.util.concurrent.ExecutorService;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Controller("/services/reports/member")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class MemberProfileReportController {

    private static final Logger LOG = LoggerFactory.getLogger(com.objectcomputing.checkins.services.memberprofile.MemberProfileController.class);
    private final MemberProfileReportServices memberProfileReportServices;
    private final Scheduler ioScheduler;

    public MemberProfileReportController(MemberProfileReportServices memberProfileReportServices,
                                         @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.memberProfileReportServices = memberProfileReportServices;
        this.ioScheduler = Schedulers.fromExecutorService(ioExecutorService);
    }

    /**
     * Read-only POST to mimic a GET with many parameters
     * @param dto The {@link MemberProfileReportQueryDTO} containing the UUIDs of the members to include in the generated CSV
     * @return HTTP response with the CSV file
     */
    @Post(produces = MediaType.TEXT_CSV)
    @RequiredPermission(Permissions.CAN_VIEW_PROFILE_REPORT)
    public Mono<MutableHttpResponse<File>> getCsvFile(@Nullable @Body MemberProfileReportQueryDTO dto) {
        return Mono.defer(() -> Mono.just(memberProfileReportServices.generateFile(dto)))
                .subscribeOn(ioScheduler)
                .map(file -> HttpResponse
                        .ok(file)
                        .header("Content-Disposition", String.format("attachment; filename=%s", file.getName())))
                .onErrorResume(error -> {
                    LOG.error("Something went terribly wrong during export... ", error);
                    return Mono.just(HttpResponse.serverError());
                });
    }

}

