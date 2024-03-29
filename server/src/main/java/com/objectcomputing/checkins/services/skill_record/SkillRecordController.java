package com.objectcomputing.checkins.services.skill_record;

import com.objectcomputing.checkins.security.permissions.Permissions;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.util.concurrent.ExecutorService;

@Controller("/services/skills/records")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class SkillRecordController {

    private static final Logger LOG = LoggerFactory.getLogger(SkillRecordController.class);
    private final SkillRecordServices skillRecordServices;
    private final Scheduler ioScheduler;

    public SkillRecordController(
            SkillRecordServices skillRecordServices,
            @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.skillRecordServices = skillRecordServices;
        this.ioScheduler = Schedulers.fromExecutor(ioExecutorService);
    }

    @RequiredPermission(Permissions.CAN_VIEW_SKILL_CATEGORIES)
    @Get(value = "/csv", produces = MediaType.TEXT_CSV)
    public Mono<MutableHttpResponse<File>> generateCsv() {
        return Mono.defer(() -> Mono.just(skillRecordServices.generateFile()))
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
