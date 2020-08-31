package com.objectcomputing.checkins.services.checkins;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServicesImpl;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
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
import io.micronaut.security.utils.SecurityService;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import io.micronaut.http.annotation.Error;

@Controller("/services/check-in")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="check-ins")
public class CheckInController {
    
    @Inject
    CheckInServices checkInservices ;

    @Inject
    CurrentUserServicesImpl currentUserServices;

    @Inject
    SecurityService securityService;

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

        MemberProfile currentUser = currentUserServices.currentUserDetails();
        Boolean isAdmin = securityService.hasRole(RoleType.Constants.ADMIN_ROLE);

        Set<CheckIn> checkInResult = checkInservices.findByFields(teamMemberId, pdlId,completed);

        if(isAdmin ||
                checkInResult.stream().allMatch(checkIn -> checkIn.getTeamMemberId().equals(currentUser.getUuid())) ||
                checkInResult.stream().anyMatch(checkIn -> checkIn.getPdlId().equals(currentUser.getUuid()))) {
            return checkInResult;
        }

        return null;
    }

    /**
     * Save check-in details.
     * @param checkIn
     * @return
     */
    @Post()
    public HttpResponse<CheckIn> createCheckIn(@Body @Valid CheckInCreateDTO checkIn, HttpRequest<CheckInCreateDTO> request) {

        MemberProfile currentUser = currentUserServices.currentUserDetails();
        Boolean isAdmin = securityService.hasRole(RoleType.Constants.ADMIN_ROLE);

        if(currentUser.getUuid().equals(checkIn.getTeamMemberId()) || currentUser.getUuid().equals(checkIn.getPdlId()) || isAdmin) {
            CheckIn newMemberCheckIn = checkInservices.save(new CheckIn(checkIn.getTeamMemberId(), checkIn.getPdlId(), checkIn.getCheckInDate(), checkIn.isCompleted()));
            return HttpResponse.created(newMemberCheckIn)
                    .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), newMemberCheckIn.getId()))));
        }

        return HttpResponse.status(HttpStatus.FORBIDDEN);
    }

    /**
     * Update check in details
     * @param checkIn
     * @return
     */
    @Put("/")
    public HttpResponse<?> update(@Body @Valid CheckIn checkIn, HttpRequest<CheckInCreateDTO> request) {

        MemberProfile currentUser = currentUserServices.currentUserDetails();
        Boolean isAdmin = securityService.hasRole(RoleType.Constants.ADMIN_ROLE);

        if(currentUser.getUuid().equals(checkIn.getTeamMemberId()) || currentUser.getUuid().equals(checkIn.getPdlId()) || isAdmin) {

            CheckIn updatedMemberCheckIn = checkInservices.update(checkIn);
            return HttpResponse
                    .ok()
                    .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedMemberCheckIn.getId()))))
                    .body(updatedMemberCheckIn);
        }

        return HttpResponse.status(HttpStatus.FORBIDDEN);
    }

    /**
     * 
     * @param id
     * @return
     */
    @Get("/{id}")
    public CheckIn readCheckIn(@NotNull UUID id){

        MemberProfile currentUser = currentUserServices.currentUserDetails();
        Boolean isAdmin = securityService.hasRole(RoleType.Constants.ADMIN_ROLE);
        CheckIn checkin = checkInservices.read(id);

        if(currentUser.getUuid().equals(checkin.getTeamMemberId()) || currentUser.getUuid().equals(checkin.getPdlId()) || isAdmin) {
            return checkin;
        }

        return null;
    }
}