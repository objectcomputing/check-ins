package com.objectcomputing.checkins.services.memberprofile.anniversaryreport;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Controller("/services/reports/anniversaries")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "member anniversaries")
public class AnniversaryReportController {

    private final AnniversaryServices anniversaryServices;

    public AnniversaryReportController(AnniversaryServices anniversaryServices) {
        this.anniversaryServices = anniversaryServices;
    }

    /**
     * Find anniversary or anniversaries given a month, or if blank get all anniversaries.
     *
     * @param month month of the anniversary
     * @return list of anniversaries
     */
    @Get("/{?month}")
    @RequiredPermission(Permission.CAN_VIEW_ANNIVERSARY_REPORT)
    public List<AnniversaryReportResponseDTO> findByValue(@Nullable String[] month) {
        return anniversaryServices.findByValue(month);
    }
}
