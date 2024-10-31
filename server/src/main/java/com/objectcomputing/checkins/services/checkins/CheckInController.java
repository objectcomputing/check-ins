package com.objectcomputing.checkins.services.checkins;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@Controller("/services/check-ins")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
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
    public Set<CheckIn> findCheckIns(@Nullable UUID teamMemberId, @Nullable UUID pdlId, @Nullable Boolean completed) {
        return checkInServices.findByFields(teamMemberId, pdlId, completed);
    }

    /**
     * Create and save a new CheckIn.
     *
     * @param checkIn, {@link CheckInCreateDTO}
     * @return {@link HttpResponse<CheckIn>}
     */
    @Post
    @RequiredPermission(Permission.CAN_CREATE_CHECKINS)
    public HttpResponse<CheckIn> createCheckIn(@Body @Valid CheckInCreateDTO checkIn, HttpRequest<?> request) {
        CheckIn createdCheckIn = checkInServices.save(new CheckIn(checkIn.getTeamMemberId(), checkIn.getPdlId(), checkIn.getCheckInDate(), checkIn.isCompleted()));
        return HttpResponse.created(createdCheckIn)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdCheckIn.getId()))));
    }

    /**
     * Update a CheckIn
     *
     * @param checkIn, {@link CheckIn}
     * @return {@link HttpResponse<CheckIn>}
     */
    @Put
    @RequiredPermission(Permission.CAN_UPDATE_CHECKINS)
    public HttpResponse<CheckIn> update(@Body @Valid @NotNull CheckIn checkIn, HttpRequest<?> request) {
        CheckIn updatedCheckIn = checkInServices.update(checkIn);
        return HttpResponse.ok(updatedCheckIn)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedCheckIn.getId()))));
    }

    /**
     * @param id {@link UUID} the id of the check-in to read
     * @return {@link CheckIn} the check-in
     */
    @Get("/{id}")
    @RequiredPermission(Permission.CAN_VIEW_CHECKINS)
    public CheckIn readCheckIn(@NotNull UUID id) {
        return checkInServices.read(id);
    }
}
