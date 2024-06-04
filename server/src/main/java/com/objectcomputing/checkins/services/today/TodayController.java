package com.objectcomputing.checkins.services.today;

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
    public Mono<HttpResponse<TodayResponseDTO>> getTodaysEvents() {
        return Mono.fromCallable(todayServices::getTodaysEvents)
                .map(HttpResponse::ok);
    }
}
