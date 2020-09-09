package com.objectcomputing.checkins.services.checkins;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import io.micronaut.http.annotation.Error;

@Controller("/services/check-in")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="check-ins")
@Singleton
public class CheckInController {

    private CheckInServices checkInServices;

    public CheckInController(CheckInServices checkInServices) {
        this.checkInServices = checkInServices;
    }

    @Error(exception = CheckInBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, CheckInBadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    /**
     * Find Check-in details by Member Id or PDL Id. 
     * @param teamMemberId
     * @param pdlId
     * @return
     */
    @Get("/{?teamMemberId,pdlId,completed}")
    public Set<CheckIn> findByValue(@Nullable UUID teamMemberId, @Nullable UUID  pdlId, @Nullable Boolean completed) {
        return checkInServices.findByFields(teamMemberId, pdlId, completed);
    }

    /**
     * Save check-in details.
     * @param checkIn
     * @return
     */
    @Post()
    public HttpResponse<CheckIn> createCheckIn(@Body @Valid CheckInCreateDTO checkIn, HttpRequest<CheckInCreateDTO> request) {

        CheckIn newMemberCheckIn = checkInServices.save(new CheckIn(checkIn.getTeamMemberId(), checkIn.getPdlId(), checkIn.getCheckInDate(), checkIn.isCompleted()));
        return HttpResponse.created(newMemberCheckIn)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), newMemberCheckIn.getId()))));
    }

    /**
     * Update check in details
     * @param checkIn
     * @return
     */
    @Put("/")
    public HttpResponse<?> update(@Body @Valid CheckIn checkIn, HttpRequest<CheckInCreateDTO> request) {

        CheckIn updatedMemberCheckIn = checkInServices.update(checkIn);
        return HttpResponse
                .ok()
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedMemberCheckIn.getId()))))
                .body(updatedMemberCheckIn);
    }

    /**
     *
     * @param id
     * @return
     */
    @Get("/{id}")
    public CheckIn readCheckIn(@NotNull UUID id){
        return checkInServices.read(id);
    }
}