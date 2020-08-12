package com.objectcomputing.checkins.services.memberSkill;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller("/services/member-skill")
@Secured(SecurityRule.IS_ANONYMOUS)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "member-skill")
public class MemberSkillController {

    @Inject
    private MemberSkillServices memberSkillsService;

    @Error(exception = MemberSkillBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, MemberSkillBadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    /**
     * Create and save a new member skill.
     *
     * @param memberSkill, {@link MemberSkillCreateDTO}
     * @return {@link HttpResponse< MemberSkill >}
     */

    @Post(value = "/")
    public HttpResponse<MemberSkill> createAMemberSkill(@Body @Valid @NotNull MemberSkillCreateDTO memberSkill) {
        MemberSkill newMemberSkill = memberSkillsService.save(new MemberSkill(memberSkill.getMemberid(), memberSkill.getSkillid()));

        if (newMemberSkill == null) {
            return HttpResponse.status(HttpStatus.valueOf(409), "already exists");
        } else {
            return HttpResponse
                    .created(newMemberSkill)
                    .headers(headers -> headers.location(location(newMemberSkill.getId())));
        }
    }

    /**
     * Delete Member skill
     *
     * @param id, id of {@link MemberSkill} to delete
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteMemberSkill(UUID id) {
        memberSkillsService.delete(id);
        return HttpResponse
                .ok();
    }

    /**
     * Get all MemberSkills
     *
     * @return {@link Set < MemberSkill >}
     */
    @Get("/all")
    public Set<MemberSkill> readAll() {
        return memberSkillsService.readAll();
    }

    /**
     * Get MemberSkill based off id
     *
     * @param id {@link UUID} of the member skill entry
     * @return {@link MemberSkill}
     */

    @Get("/{id}")
    public MemberSkill readMemberSkill(UUID id) {
        return memberSkillsService.read(id);
    }

    /**
     * Find member skills that match all filled in parameters, return all results when given no params
     *
     * @param memberid {@link UUID} of member profile
     * @param skillid  {@link UUID} of skills
     * @return {@link List < CheckIn > list of checkins}
     */
    @Get("/{?memberid,skillid}")
    public Set<MemberSkill> findMemberSkills(@Nullable UUID memberid,
                                             @Nullable UUID skillid) {
        return memberSkillsService.findByFields(memberid, skillid);
    }


    protected URI location(UUID uuid) {
        return URI.create("/services/member-skill/" + uuid);
    }

}
