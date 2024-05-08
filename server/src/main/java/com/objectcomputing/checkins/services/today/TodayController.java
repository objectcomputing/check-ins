package com.objectcomputing.checkins.services.today;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutorService;

@Controller("/services/today")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Today")
public class TodayController {


    private final TodayServices todayServices;
    private final EventLoopGroup eventLoopGroup;
    private final Scheduler scheduler;

    public TodayController(TodayServices todayServices,
                           EventLoopGroup eventLoopGroup,
                           @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.todayServices = todayServices;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = Schedulers.fromExecutorService(ioExecutorService);
    }

    /**
     * Get a summary of today's events
     *
     * @return {@link TodayResponseDTO today's events}
     */

    @Get()
    public Mono<HttpResponse<TodayResponseDTO>> getTodaysEvents() {
        return Mono.fromCallable(todayServices::getTodaysEvents)
                .publishOn(Schedulers.fromExecutor(eventLoopGroup)).subscribeOn(scheduler)
                .map(todaysEvents -> (HttpResponse<TodayResponseDTO>) HttpResponse.ok(todaysEvents))
                .subscribeOn(scheduler);
    }
}
