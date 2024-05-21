package com.objectcomputing.checkins.services.memberprofile.birthday;

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

@Controller("/services/reports/birthdays")
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Member Birthday")
public class BirthDayController {


    private final BirthDayServices birthDayServices;

    public BirthDayController(BirthDayServices birthDayServices) {
        this.birthDayServices = birthDayServices;
    }

    /**
     * Find birthdays given a month, or if blank get all birthdays.
     *
     * @param month,    month of the birthday
     * @return {@link Set < BirthDayResponseDTO > list of birthdays}
     */

    @Get("/{?month,dayOfMonth}")
    @RequiredPermission(Permission.CAN_VIEW_BIRTHDAY_REPORT)
    public Mono<HttpResponse<List<BirthDayResponseDTO>>> findByValue(@Nullable String[] month, @Nullable Integer[] dayOfMonth) {
        return Mono.fromCallable(() -> birthDayServices.findByValue(month, dayOfMonth))
                .map(HttpResponse::ok);
    }
}
