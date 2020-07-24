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
import io.swagger.v3.oas.annotations.tags.Tag;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Consumes;

import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Controller("/member-profile")
@Secured(SecurityRule.IS_ANONYMOUS)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="member profile")
public class MemberProfileController {

    protected final MemberProfileRepository memberProfileRepository;

    public MemberProfileController(MemberProfileRepository memberProfileRepository){
        this.memberProfileRepository = memberProfileRepository;
    }

    /**
     * Find Team Member profile by UUID.
     * @param uuid
     * @return
     */
    @Get("/{uuid}")
    public HttpResponse<MemberProfile> getByUuid(UUID uuid) {
        
        if(!memberProfileRepository.existsById(uuid)) {
            return HttpResponse.notFound();
        }

        MemberProfile result = memberProfileRepository.findByUuid(uuid);
        return HttpResponse
            .ok(result)
            .headers(headers -> headers.location(location(result.getUuid())));
    }

    /**
     * Find Team Member profile by Name, Role, PdlId or find all.
     * @param name
     * @param role
     * @param pdlId
     * @return
     */
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

    /**
     * Save a new team member profile.
     * @param memberProfile
     * @return
     */
    @Post("/")
    public HttpResponse<MemberProfile> save(@Body @Valid MemberProfile memberProfile) {
        MemberProfile newMemberProfile = memberProfileRepository.save(memberProfile);
        
        return HttpResponse
                .created(newMemberProfile)
                .headers(headers -> headers.location(location(newMemberProfile.getUuid())));
    }

    /**
     * Update a Team member profile.
     * @param memberProfile
     * @return
     */
    @Put("/")
    public HttpResponse<MemberProfile> update(@Body @Valid MemberProfile memberProfile) {

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
        return URI.create("/member-profile/" + uuid);
    }
}
