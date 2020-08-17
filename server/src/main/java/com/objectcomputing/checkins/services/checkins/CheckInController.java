package com.objectcomputing.checkins.services.checkins;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.Valid;

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
@Secured(SecurityRule.IS_ANONYMOUS)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="check-ins")
public class CheckInController {
    
    @Inject
    CheckInServices checkInservices ;

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
    @Get("/{?teamMemberId,pdlId}")
    public Set<CheckIn> findByValue(@Nullable UUID teamMemberId,
                                     @Nullable UUID  pdlId) {          
            return checkInservices.findByFields(teamMemberId, pdlId);
    }

    /**
     * Save check-in details.
     * @param checkIn
     * @return
     */
    @Post("/")
    public HttpResponse<CheckIn> createCheckIn(@Body @Valid CheckInCreateDTO checkIn, HttpRequest<CheckInCreateDTO> request) {
        CheckIn newMemberCheckIn = checkInservices.save(new CheckIn(checkIn.getPdlId(),checkIn.getTeamMemberId(),checkIn.getCheckInDate()));     
        return HttpResponse.created(newMemberCheckIn)
        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(),newMemberCheckIn.getId()))));
    }

    /**
     * Update check in details
     * @param checkIn
     * @return
     */
    @Put("/")
    public HttpResponse<?> update(@Body @Valid CheckIn checkIn,HttpRequest<CheckInCreateDTO> request) {

            CheckIn updatedMemberCheckIn = checkInservices.update(checkIn);
            return HttpResponse
                    .ok()
                    .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(),updatedMemberCheckIn.getId()))))
                    .body(updatedMemberCheckIn);
                         
    }
    
    /**
     * 
     * @return
     */
    @Get("/all") 
    public Set<CheckIn> readAll() {
        return checkInservices.readAll();
    }

    /**
     * 
     * @param id
     * @return
     */
    @Get("/{id}")
    public CheckIn readCheckIn(UUID id){
        return checkInservices.read(id);
    }


}