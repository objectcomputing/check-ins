package com.objectcomputing.checkins.services.memberprofile.birthday;


import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.core.annotation.Nullable;
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
    private final ExecutorService ioExecutorService;

    public BirthDayController(BirthDayServices birthDayServices,
                                       EventLoopGroup eventLoopGroup,
                                       ExecutorService ioExecutorService) {
        this.birthDayServices = birthDayServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Find birthdays given a month, or if blank get all birthdays.
     *
     * @param month,    month of the birthday
     * @return {@link Set < BirthDayResponseDTO > list of birthdays}
     */

    @Get("/{?month}")
    public Single<HttpResponse<List<BirthDayResponseDTO>>> findByValue(@Nullable String month) {

        return Single.fromCallable(() -> birthDayServices.findByValue(month))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(birthdays -> (HttpResponse<List<BirthDayResponseDTO>>) HttpResponse.ok(birthdays))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }
}
