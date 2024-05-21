package com.objectcomputing.checkins.services.memberprofile.csvreport;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
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

@Controller(MemberProfileReportController.PATH)
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
class MemberProfileReportController {
    public static final String PATH = "/services/reports/member";
    private static final Logger LOG = LoggerFactory.getLogger(com.objectcomputing.checkins.services.memberprofile.MemberProfileController.class);
    private final MemberProfileReportServices memberProfileReportServices;

    MemberProfileReportController(MemberProfileReportServices memberProfileReportServices) {
        this.memberProfileReportServices = memberProfileReportServices;
    }

    /**
     * Read-only POST to mimic a GET with many parameters
     * @param dto The {@link MemberProfileReportQueryDTO} containing the UUIDs of the members to include in the generated CSV
     * @return HTTP response with the CSV file
     */
    @Post(produces = MediaType.TEXT_CSV)
    @RequiredPermission(Permission.CAN_VIEW_PROFILE_REPORT)
    HttpResponse<File> getCsvFile(@Nullable @Body MemberProfileReportQueryDTO dto) {
        try {
            File file = memberProfileReportServices.generateFile(dto);
            return HttpResponse
                    .ok(file)
                    .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", file.getName()));
        } catch (Exception error) {
            LOG.error("Something went terribly wrong during export... ", error);
            return HttpResponse.serverError();
        }
    }
}

