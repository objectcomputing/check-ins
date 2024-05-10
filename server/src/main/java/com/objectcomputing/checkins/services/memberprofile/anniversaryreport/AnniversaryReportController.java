package com.objectcomputing.checkins.services.memberprofile.anniversaryreport;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Controller("/services/reports/anniversaries")
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "member anniversaries")
public class AnniversaryReportController {

    private final AnniversaryServices anniversaryServices;

    public AnniversaryReportController(AnniversaryServices anniversaryServices) {
        this.anniversaryServices = anniversaryServices;
    }

    /**
     * Find anniversary or anniversaries given a month, or if blank get all anniversaries.
     *
     * @param month,    month of the anniversary
     * @return {@link Set < AnniversaryReportResponseDTO > list of anniversaries}
     */

    @Get("/{?month}")
    @RequiredPermission(Permission.CAN_VIEW_ANNIVERSARY_REPORT)
    public Mono<HttpResponse<List<AnniversaryReportResponseDTO>>> findByValue(@Nullable String[] month) {
        return Mono.fromCallable(() -> anniversaryServices.findByValue(month))
                .map(HttpResponse::ok);
    }
}
