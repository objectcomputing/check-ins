package com.objectcomputing.checkins.services.memberprofile;

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
import io.micronaut.http.annotation.Produces;

@Controller("/member-profile")
@Produces(MediaType.APPLICATION_JSON)
public class MemberProfileController {

    protected final MemberProfileRepository memberProfileRepository;

    public MemberProfileController(MemberProfileRepository memberProfileRepository){
        this.memberProfileRepository = memberProfileRepository;
    }

    @Get("/{?name,role,pdlId}")
    public List<MemberProfile> findByValue(@Nullable String name, @Nullable String role, @Nullable UUID pdlId) {

        if(name != null) {
            return memberProfileRepository.findByName(name);
        } else if(role != null) {
            return memberProfileRepository.findByRole(role);
        } else if(pdlId != null) {
            return memberProfileRepository.findByPdlId(pdlId);
        } else {
            return memberProfileRepository.findAll();
        }
    }

    @Post("/")
    public HttpResponse<MemberProfile> save(@Body @Valid MemberProfile memberProfile) {
        MemberProfile newMemberProfile = memberProfileRepository.save(memberProfile);
        
        return HttpResponse
                .created(newMemberProfile)
                .headers(headers -> headers.location(location(newMemberProfile.getUuid())));
    }

    @Put("/")
    public HttpResponse<?> update(@Body @Valid MemberProfile memberProfile) {

        if(null != memberProfile.getUuid()) {
            MemberProfile updatedMemberProfile = memberProfileRepository.update(memberProfile);
            return HttpResponse
                    .ok()
                    .headers(headers -> headers.location(location(updatedMemberProfile.getUuid())))
                    .body(updatedMemberProfile);
                    
        }
        
        return HttpResponse.badRequest();
    }

    protected URI location(UUID uuid) {
        return URI.create("/team-profile/" + uuid);
    }
}
