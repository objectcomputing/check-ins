package com.objectcomputing.checkins.services.memberprofile.birthday;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Controller("/services/reports/birthdays")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "Member Birthday")
public class BirthDayController {

    private final BirthDayServices birthDayServices;

    public BirthDayController(BirthDayServices birthDayServices) {
        this.birthDayServices = birthDayServices;
    }

    /**
     * Find birthdays given a month, or if blank get all birthdays.
     *
     * @param month month of the birthday
     * @return list of birthdays
     */
    @Get("/{?month,dayOfMonth}")
    public List<BirthDayResponseDTO> findByValue(@Nullable String[] month, @Nullable Integer[] dayOfMonth) {
        return birthDayServices.findByValue(month, dayOfMonth);
    }
}
