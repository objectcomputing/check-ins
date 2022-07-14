package com.objectcomputing.checkins.services.memberprofile.birthday;


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

import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

@Controller("/services/reports/birthdays")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Member Birthday")
public class BirthDayController {


    private final BirthDayServices birthDayServices;
    private final EventLoopGroup eventLoopGroup;
    private final Scheduler scheduler;

    public BirthDayController(BirthDayServices birthDayServices,
                              EventLoopGroup eventLoopGroup,
                              @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.birthDayServices = birthDayServices;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = Schedulers.fromExecutorService(ioExecutorService);
    }

    /**
     * Find birthdays given a month, or if blank get all birthdays.
     *
     * @param month,    month of the birthday
     * @return {@link Set < BirthDayResponseDTO > list of birthdays}
     */

    @Get("/{?month}")
    public Mono<HttpResponse<List<BirthDayResponseDTO>>> findByValue(@Nullable String[] month) {

        return Mono.fromCallable(() -> birthDayServices.findByValue(month))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(birthdays -> (HttpResponse<List<BirthDayResponseDTO>>) HttpResponse.ok(birthdays))
                .subscribeOn(scheduler);
    }
}
