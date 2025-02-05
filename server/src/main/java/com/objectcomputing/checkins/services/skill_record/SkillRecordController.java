package com.objectcomputing.checkins.services.skill_record;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

@Controller("/services/skills/records")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
class SkillRecordController {
    private static final Logger LOG = LoggerFactory.getLogger(SkillRecordController.class);
    private final SkillRecordServices skillRecordServices;

    SkillRecordController(SkillRecordServices skillRecordServices) {
        this.skillRecordServices = skillRecordServices;
    }

    @Get(value = "/csv", produces = MediaType.TEXT_CSV)
    HttpResponse<File> generateCsv() {
        try {
            File file = skillRecordServices.generateFile();
            return HttpResponse.ok(file)
                    .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", file.getName()));
        } catch (IOException error) {
            LOG.error("Something went terribly wrong during export... ", error);
            return HttpResponse.serverError();
        }
    }
}
