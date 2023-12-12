package com.objectcomputing.checkins.services.checkins;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.security.permissions.Permissions;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/check-ins")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "check-ins")
public class CheckInController {

    private final CheckInServices checkInServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public CheckInController(CheckInServices checkInServices,
                             EventLoopGroup eventLoopGroup,
                             @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.checkInServices = checkInServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Find Check-in details by Member Id or PDL Id.
     *
     * @param teamMemberId
     * @param pdlId
     * @return
     */
    @Get("/{?teamMemberId,pdlId,completed}")
    @RequiredPermission(Permissions.CAN_VIEW_CHECKINS)
    public Mono<HttpResponse<Set<CheckIn>>> findCheckIns(@Nullable UUID teamMemberId, @Nullable UUID pdlId, @Nullable Boolean completed) {
        return Mono.fromCallable(() -> checkInServices.findByFields(teamMemberId, pdlId, completed))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(createdCheckIn -> (HttpResponse<Set<CheckIn>>) HttpResponse.ok(createdCheckIn))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Create and save a new CheckIn.
     *
     * @param checkIn, {@link CheckInCreateDTO}
     * @return {@link HttpResponse<CheckIn>}
     */

    @Post("/")
    @RequiredPermission(Permissions.CAN_CREATE_CHECKINS)
    public Mono<HttpResponse<CheckIn>> createCheckIn(@Body @Valid CheckInCreateDTO checkIn,
                                                       HttpRequest<CheckInCreateDTO> request) {
        return Mono.fromCallable(() -> checkInServices.save(new CheckIn(checkIn.getTeamMemberId(), checkIn.getPdlId(), checkIn.getCheckInDate(), checkIn.isCompleted())))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(createdCheckIn -> {
                    return (HttpResponse<CheckIn>) HttpResponse
                            .created(createdCheckIn)
                            .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdCheckIn.getId()))));
                }).subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Update a CheckIn
     *
     * @param checkIn, {@link CheckIn}
     * @return {@link HttpResponse<CheckIn>}
     */
    @Put("/")
    public Mono<HttpResponse<CheckIn>> update(@Body @Valid @NotNull CheckIn checkIn,
                                                HttpRequest<CheckIn> request) {
        return Mono.fromCallable(() -> checkInServices.update(checkIn))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(updatedCheckIn -> (HttpResponse<CheckIn>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedCheckIn.getId()))))
                        .body(updatedCheckIn))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));

    }

    /**
     * @param id
     * @return
     */
    @Get("/{id}")
    @RequiredPermission(Permissions.CAN_VIEW_CHECKINS)
    public Mono<HttpResponse<CheckIn>> readCheckIn(@NotNull UUID id) {
        return Mono.fromCallable(() -> checkInServices.read(id))
                .switchIfEmpty(Mono.error(new NotFoundException("No checkin for UUID")))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(checkIn -> (HttpResponse<CheckIn>) HttpResponse.ok(checkIn))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));

    }
}
