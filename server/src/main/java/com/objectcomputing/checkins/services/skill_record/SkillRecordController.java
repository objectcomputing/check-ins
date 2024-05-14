package com.objectcomputing.checkins.services.skill_record;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.io.File;

@Controller("/services/skills/records")
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
public class SkillRecordController {

    private static final Logger LOG = LoggerFactory.getLogger(SkillRecordController.class);
    private final SkillRecordServices skillRecordServices;

    public SkillRecordController(
            SkillRecordServices skillRecordServices) {
        this.skillRecordServices = skillRecordServices;
    }

    @RequiredPermission(Permission.CAN_VIEW_SKILL_CATEGORIES)
    @Get(value = "/csv", produces = MediaType.TEXT_CSV)
    public Mono<MutableHttpResponse<File>> generateCsv() {
        return Mono.defer(() -> Mono.just(skillRecordServices.generateFile()))
                .map(file -> HttpResponse.ok(file)
                        .header("Content-Disposition", String.format("attachment; filename=%s", file.getName())))
                .onErrorResume(error -> {
                    LOG.error("Something went terribly wrong during export... ", error);
                    return Mono.just(HttpResponse.serverError());
                });
    }
}
