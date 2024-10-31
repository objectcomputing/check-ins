package com.objectcomputing.checkins.services.today;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller("/services/today")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "Today")
public class TodayController {

    private final TodayServices todayServices;

    public TodayController(TodayServices todayServices) {
        this.todayServices = todayServices;
    }

    /**
     * Get a summary of today's events
     *
     * @return {@link TodayResponseDTO today's events}
     */
    @Get
    public TodayResponseDTO getTodaysEvents() {
        return todayServices.getTodaysEvents();
    }
}
