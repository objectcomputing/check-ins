package com.objectcomputing.checkins.services.checkins;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.Valid;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Controller("/check-in")
@Secured(SecurityRule.IS_ANONYMOUS)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name="check-ins")
public class CheckInController {
    
    protected final CheckInRepository checkInRepository;

    public CheckInController(CheckInRepository checkInRepository){
        this.checkInRepository = checkInRepository;
    }
    /**
     * Find Check-in details by Member Id, Year, Quarter, PDL Id or find all. 
     * @param teamMemberId
     * @param targetYear
     * @param pdlId
     * @param targetQtr
     * @return
     */
    @Get("/{?teamMemberId,targetYear,targetQtr,pdlId}")
    public List<CheckIn> findByValue(@Nullable UUID teamMemberId, @Nullable String targetYear,
                                     @Nullable UUID  pdlId, @Nullable String targetQtr) {          

       if(teamMemberId != null) {
            return checkInRepository.findByTeamMemberId(teamMemberId);
        } else if(targetYear != null) {
            return checkInRepository.findByTargetYearAndTargetQtr(targetYear,targetQtr);
        } else if(pdlId != null) {
            return checkInRepository.findByPdlId(pdlId);
        } else {
            return checkInRepository.findAll();
        }
    }

    /**
     * Save check-in details.
     * @param checkIn
     * @return
     */
    @Post("/")
    public HttpResponse<CheckIn> save(@Body @Valid CheckIn checkIn) {
        CheckIn newMemberCheckIn = checkInRepository.save(checkIn);
        
        return HttpResponse.created(newMemberCheckIn)
                .headers(headers -> headers.location(location(newMemberCheckIn.getId())));
    }

    /**
     * Update check in details
     * @param checkIn
     * @return
     */
    @Put("/")
    public HttpResponse<?> update(@Body @Valid CheckIn checkIn) {

        if(null != checkIn.getId()){
            CheckIn updatedMemberCheckIn = checkInRepository.update(checkIn);
            return HttpResponse
                    .ok()
                    .headers(headers -> headers.location(location(updatedMemberCheckIn.getId())))
                    .body(updatedMemberCheckIn);
                    
        }
        
        return HttpResponse.badRequest();
    }

	private URI location(UUID id) {
        return URI.create("/check-in/" + id);
	}

}