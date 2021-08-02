package com.objectcomputing.checkins.services.memberprofile.anniversaryreport;

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

@Controller("/services/reports/anniversaries")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "member anniversaries")
public class AnniversaryReportController {

    private final AnniversaryServices anniversaryServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public AnniversaryReportController(AnniversaryServices anniversaryServices,
                                       EventLoopGroup eventLoopGroup,
                                       ExecutorService ioExecutorService) {
        this.anniversaryServices = anniversaryServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Find anniversary or anniversaries given a month, or if blank get all anniversaries.
     *
     * @param month,    month of the anniversary
     * @return {@link Set < AnniversaryReportResponseDTO > list of anniversaries}
     */

    @Get("/{?month}")
    public Single<HttpResponse<List<AnniversaryReportResponseDTO>>> findByValue(@Nullable String month) {

        return Single.fromCallable(() -> anniversaryServices.findByValue(month))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(anniversaries -> (HttpResponse<List<AnniversaryReportResponseDTO>>) HttpResponse.ok(anniversaries))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }
}
