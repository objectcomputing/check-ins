package com.objectcomputing;

import java.net.URI;
import java.util.UUID;

import javax.validation.Valid;

import com.objectcomputing.checkins.CheckIns;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;

@Controller("/check-in")
public class CheckInsController {

    protected final CheckInsRepository checkInsRepository;

    public CheckInsController(CheckInsRepository checkInsRepository){
        this.checkInsRepository = checkInsRepository;
    }

    @Post("/")
    public HttpResponse<CheckIns> save(@Body @Valid CheckIns checkIns) {
        CheckIns newMemberCheckIn = checkInsRepository.createMemberCheckIn(checkIns);
        
        return HttpResponse.created(newMemberCheckIn)
                .headers(headers -> headers.location(location(newMemberCheckIn.getPdlId())));
    }

	private URI location(UUID pdlId) {
        return URI.create("/check-in/" + pdlId);
	}

    //TODO - Implement other methods from CheckInsRepostory.java
}