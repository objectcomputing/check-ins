package com.objectcomputing.checkins.services.checkins;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.Valid;

import com.objectcomputing.checkins.services.checkins.CheckIns;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;

@Controller("/check-in")
public class CheckInsController {

    protected final CheckInsRepository checkInsRepository;

    public CheckInsController(CheckInsRepository checkInsRepository){
        this.checkInsRepository = checkInsRepository;
    }

    @Get("/{?teamMemberId,targetYear,targetQtr,pdlId}")
    public List<CheckIns> findByValue(@Nullable UUID teamMemberId, @Nullable String targetYear, @Nullable UUID  pdlId
                                            ,@Nullable String targetQtr) {          

        if(teamMemberId != null) {
            return checkInsRepository.findByName(teamMemberId);
        } else if(targetYear != null) {
            return checkInsRepository.findByTargetQuarter(targetYear,targetQtr);
        } else if(pdlId != null) {
            return checkInsRepository.findByPdlId(pdlId);
        } else {
            return checkInsRepository.findAll();
        }
    }


    @Post("/")
    public HttpResponse<CheckIns> save(@Body @Valid CheckIns checkIns) {
        CheckIns newMemberCheckIn = checkInsRepository.save(checkIns);
        
        return HttpResponse.created(newMemberCheckIn)
                .headers(headers -> headers.location(location(newMemberCheckIn.getPdlId())));
    }


    @Put("/")
    public HttpResponse<?> update(@Body @Valid CheckIns checkIn) {

        if(null != checkIn.getTeamMemberId()){
            CheckIns updatedMemberCheckIn = checkInsRepository.update(checkIn);
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