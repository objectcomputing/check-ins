package com.objectcomputing.checkins.services.pulseresponse;

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
import io.micronaut.http.annotation.Put;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.micronaut.http.annotation.Produces;

import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Controller("/pulse-response")
@Secured(SecurityRule.IS_ANONYMOUS)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name="pulse response")
public class PulseResponseController {
    
    protected final PulseResponseRepository pulseResponseRepository;

    public PulseResponseController(PulseResponseRepository pulseResponseRepository){
        this.pulseResponseRepository = pulseResponseRepository;
    }
    /**
     * Find Pulse Response by Team Member or Date Range.
    //  * @param name
    //  * @param role
    //  * @param pdlId
    //  * @return
    //  */
    // @Get("/{?name,role,pdlId}")
    // public List<MemberProfile> findByValue(@Nullable String name, @Nullable String role, @Nullable UUID pdlId) {

    //     if(name != null) {
    //         return memberProfileRepository.findByName(name);
    //     } else if(role != null) {
    //         return memberProfileRepository.findByRole(role);
    //     } else if(pdlId != null) {
    //         return memberProfileRepository.findByPdlId(pdlId);
    //     } else {
    //         return memberProfileRepository.findAll();
    //     }
    // }

    /**
     * Save a new Pulse Response.
     * @param pulseResponse
     * @return
     */
    @Post("/")
    public HttpResponse<PulseResponse>save(@Body @Valid PulseResponse pulseResponse) {
        PulseResponse newPulseResponse = pulseResponseRepository.save(pulseResponse);
        
        return HttpResponse
                .created(newPulseResponse)
                .headers(headers -> headers.location(location(newPulseResponse.getId())));
    }

    /**
     * Update a Pulse Response.
     * @param pulseResponse
     * @return
     */
    @Put("/")
    public HttpResponse<?> update(@Body @Valid PulseResponse pulseResponse) {

        if(null != pulseResponse.getId()) {
            PulseResponse updatedPulseResponse = pulseResponseRepository.update(pulseResponse);
            return HttpResponse
                    .ok()
                    .headers(headers -> headers.location(location(updatedPulseResponse.getId())))
                    .body(updatedPulseResponse);
                    
        }
        
        return HttpResponse.badRequest();
    }

    protected URI location(UUID uuid) {
        return URI.create("/pulse-response/" + uuid);
    }
}