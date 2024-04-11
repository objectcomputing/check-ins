package com.objectcomputing.checkins.services.memberprofile.anniversaryreport;

;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
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

@Controller("/services/reports/anniversaries")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "member anniversaries")
public class AnniversaryReportController {

    private final AnniversaryServices anniversaryServices;
    private final EventLoopGroup eventLoopGroup;
    private final Scheduler scheduler;

    public AnniversaryReportController(AnniversaryServices anniversaryServices,
                                       EventLoopGroup eventLoopGroup,
                                       @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.anniversaryServices = anniversaryServices;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = Schedulers.fromExecutorService(ioExecutorService);
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
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(anniversaries -> (HttpResponse<List<AnniversaryReportResponseDTO>>) HttpResponse.ok(anniversaries))
                .subscribeOn(scheduler);
    }
}
