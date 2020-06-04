package com.objectcomputing;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.Valid;

import com.objectcomputing.member.MemberProfile;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;

@Controller("/team-profile")
public class MemberProfileController {

    protected final MemberProfileRepository memberProfileRepository;

    public MemberProfileController(MemberProfileRepository memberProfileRepository){
        this.memberProfileRepository = memberProfileRepository;
    }

    @Get("/{?name,role,pdlId}")
    public List<MemberProfile> findByValue(@Nullable String name, @Nullable String role, @Nullable UUID pdlId) {
        System.out.println("value requested = " + name + role + pdlId);

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
        MemberProfile newMemberProfile = memberProfileRepository.createProfile(memberProfile);
        
        return HttpResponse
                .created(newMemberProfile)
                .headers(headers -> headers.location(location(newMemberProfile.getUuid())));
    }

    @Put("/")
    public HttpResponse<?> update(@Body @Valid MemberProfile memberProfile) {
        if(null != memberProfile.getUuid()){

            int numberOfEntitiesUpdated = memberProfileRepository.update(memberProfile);
            
            return HttpResponse
            .noContent()
            .header(HttpHeaders.LOCATION, location(memberProfile.getUuid()).getPath());
        } else {
            return HttpResponse.badRequest();
        }
    }

    protected URI location(UUID uuid) {
        return URI.create("/team-profile/" + uuid);
    }

    protected URI location(MemberProfile memberProfile) {
        return location(memberProfile.getUuid());
    }
}
