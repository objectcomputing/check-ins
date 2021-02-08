package com.objectcomputing.checkins.services.checkins;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/check-in")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="check-ins")
public class CheckInController {

    private final CheckInServices checkInServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

        public CheckInController(CheckInServices checkInServices,
                                    EventLoopGroup eventLoopGroup,
                                    @Named(TaskExecutors.IO) ExecutorService ioExecutorService){
        this.checkInServices = checkInServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Find Check-in details by Member Id or PDL Id. 
     * @param teamMemberId
     * @param pdlId
     * @return
     */
    @Get("/{?teamMemberId,pdlId,completed}")
    public Single<HttpResponse<Set<CheckIn>>> findCheckIns(@Nullable UUID teamMemberId, @Nullable UUID  pdlId, @Nullable Boolean completed) {
        return Single.fromCallable(() -> checkInServices.findByFields(teamMemberId, pdlId, completed))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(createdCheckIn -> (HttpResponse<Set<CheckIn>>) HttpResponse.ok(createdCheckIn))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Create and save a new CheckIn.
     *
     * @param checkIn, {@link CheckInCreateDTO}
     * @return {@link HttpResponse<CheckIn>}
     */

    @Post("/")
    public Single<HttpResponse<CheckIn>> createCheckIn(@Body @Valid CheckInCreateDTO checkIn,
                                                                    HttpRequest<CheckInCreateDTO> request) {
        return Single.fromCallable(() -> checkInServices.save(new CheckIn(checkIn.getTeamMemberId(), checkIn.getPdlId(), checkIn.getCheckInDate(), checkIn.isCompleted())))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(createdCheckIn -> {return (HttpResponse<CheckIn>) HttpResponse
                    .created(createdCheckIn)
                    .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdCheckIn.getId()))));
                }).subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Update a CheckIn
     *
     * @param checkIn, {@link CheckIn}
     * @return {@link HttpResponse<CheckIn>}
     */
    @Put("/")
    public Single<HttpResponse<CheckIn>> update(@Body @Valid @NotNull CheckIn checkIn,
                                            HttpRequest<CheckIn> request) {
        return Single.fromCallable(() -> checkInServices.update(checkIn))
            .observeOn(Schedulers.from(eventLoopGroup))
            .map(updatedCheckIn -> (HttpResponse<CheckIn>) HttpResponse
                    .ok()
                    .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedCheckIn.getId()))))
                    .body(updatedCheckIn))
            .subscribeOn(Schedulers.from(ioExecutorService));

    }

    /**
     *
     * @param id
     * @return
     */
    @Get("/{id}")
    public Single<HttpResponse<CheckIn>> readCheckIn(@NotNull UUID id){
        return Single.fromCallable(() -> {
            CheckIn result = checkInServices.read(id);
            if (result == null) {
                throw new NotFoundException("No checkin for UUID");
            }
            return result;
        })
        .observeOn(Schedulers.from(eventLoopGroup))
        .map(checkIn -> {
            return (HttpResponse<CheckIn>)HttpResponse.ok(checkIn);
        }).subscribeOn(Schedulers.from(ioExecutorService));

    }
}