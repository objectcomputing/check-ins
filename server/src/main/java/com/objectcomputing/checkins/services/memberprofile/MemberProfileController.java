package com.objectcomputing.checkins.services.memberprofile;

import java.net.URI;
import java.util.Set;
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
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="member profile")
public class MemberProfileController {

    private final MemberProfileServices memberProfileServices;

    public MemberProfileController(MemberProfileServices memberProfileServices){
        this.memberProfileServices = memberProfileServices;
    }

    /**
     * Find Team Member profile by UUID.
     * @param uuid
     * @return
     */
    @Get("/{uuid}")
    public HttpResponse<MemberProfile> getByUuid(UUID uuid) {

        MemberProfile result = memberProfileServices.getById(uuid);

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
    public HttpResponse<Set<MemberProfile>> findByValue(@Nullable String name, @Nullable String role, @Nullable UUID pdlId) {
        return HttpResponse
                .ok(memberProfileServices.findByValues(name, role, pdlId));
    }

    /**
     * Save a new team member profile.
     * @param memberProfile
     * @return
     */
    @Post("/")
    public HttpResponse<MemberProfile> save(@Body @Valid MemberProfile memberProfile) {
        MemberProfile newMemberProfile = memberProfileServices.saveProfile(memberProfile);

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
            MemberProfile updatedMemberProfile = memberProfileServices.saveProfile(memberProfile);
            return HttpResponse
                    .ok()
                    .headers(headers -> headers.location(location(updatedMemberProfile.getUuid())))
                    .body(updatedMemberProfile);

        }

        throw new MemberProfileBadArgException("Member profile id is required");
    }

    protected URI location(UUID uuid) {
        return URI.create("/member-profile/" + uuid);
    }
}
