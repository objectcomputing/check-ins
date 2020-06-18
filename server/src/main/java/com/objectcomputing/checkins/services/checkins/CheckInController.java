package com.objectcomputing.checkins.services.checkins;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.Valid;

import com.objectcomputing.checkins.services.checkins.CheckIn;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;

@Controller("/check-in")
public class CheckInController {
    
    protected final CheckInRepository checkInRepository;

    public CheckInController(CheckInRepository checkInRepository){
        this.checkInRepository = checkInRepository;
    }

    @Get("/{?teamMemberId,targetYear,targetQtr,pdlId}")
    public List<CheckIn> findByValue(@Nullable UUID teamMemberId, @Nullable String targetYear, @Nullable UUID  pdlId
                                            ,@Nullable String targetQtr) {          

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


    @Post("/")
    public HttpResponse<CheckIn> save(@Body @Valid CheckIn checkIn) {
        CheckIn newMemberCheckIn = checkInRepository.save(checkIn);
        
        return HttpResponse.created(newMemberCheckIn)
                .headers(headers -> headers.location(location(newMemberCheckIn.getPdlId())));
    }


    @Put("/")
    public HttpResponse<?> update(@Body @Valid CheckIn checkIn) {

        if(null != checkIn.getTeamMemberId()){
            CheckIn updatedMemberCheckIn = checkInRepository.update(checkIn);
            return HttpResponse
                    .ok()
                    .headers(headers -> headers.location(location(updatedMemberCheckIn.getTeamMemberId())))
                    .body(updatedMemberCheckIn);
                    
        }
        
        return HttpResponse.badRequest();
    }

	private URI location(UUID pdlId) {
        return URI.create("/check-in/" + pdlId);
	}

}