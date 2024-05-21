package com.objectcomputing.checkins.services.checkins;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@Controller("/services/check-ins")
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "check-ins")
public class CheckInController {

    private final CheckInServices checkInServices;

    public CheckInController(CheckInServices checkInServices) {
        this.checkInServices = checkInServices;
    }

    /**
     * Find Check-in details by Member Id or PDL Id.
     *
     * @param teamMemberId
     * @param pdlId
     * @return
     */
    @Get("/{?teamMemberId,pdlId,completed}")
    @RequiredPermission(Permission.CAN_VIEW_CHECKINS)
    public Mono<HttpResponse<Set<CheckIn>>> findCheckIns(@Nullable UUID teamMemberId, @Nullable UUID pdlId, @Nullable Boolean completed) {
        return Mono.fromCallable(() -> checkInServices.findByFields(teamMemberId, pdlId, completed))
                .map(HttpResponse::ok);
    }

    /**
     * Create and save a new CheckIn.
     *
     * @param checkIn, {@link CheckInCreateDTO}
     * @return {@link HttpResponse<CheckIn>}
     */

    @Post
    @RequiredPermission(Permission.CAN_CREATE_CHECKINS)
    public Mono<HttpResponse<CheckIn>> createCheckIn(@Body @Valid CheckInCreateDTO checkIn, HttpRequest<?> request) {
        return Mono.fromCallable(() -> checkInServices.save(new CheckIn(checkIn.getTeamMemberId(), checkIn.getPdlId(), checkIn.getCheckInDate(), checkIn.isCompleted())))
                .map(createdCheckIn -> HttpResponse.created(createdCheckIn)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdCheckIn.getId())))));
    }

    /**
     * Update a CheckIn
     *
     * @param checkIn, {@link CheckIn}
     * @return {@link HttpResponse<CheckIn>}
     */
    @Put()
    @RequiredPermission(Permission.CAN_UPDATE_CHECKINS)
    public Mono<HttpResponse<CheckIn>> update(@Body @Valid @NotNull CheckIn checkIn, HttpRequest<?> request) {
        return Mono.fromCallable(() -> checkInServices.update(checkIn))
                .map(updatedCheckIn -> HttpResponse.ok(updatedCheckIn)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedCheckIn.getId())))));

    }

    /**
     * @param id
     * @return
     */
    @Get("/{id}")
    @RequiredPermission(Permission.CAN_VIEW_CHECKINS)
    public Mono<HttpResponse<CheckIn>> readCheckIn(@NotNull UUID id) {
        return Mono.fromCallable(() -> checkInServices.read(id))
                .switchIfEmpty(Mono.error(new NotFoundException("No checkin for UUID")))
                .map(HttpResponse::ok);

    }
}
