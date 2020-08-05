package com.objectcomputing.checkins.services.memberSkills;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.util.UUID;

@Controller("/services/member-skill")
@Secured(SecurityRule.IS_ANONYMOUS)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "member-skill")
public class MemberSkillController {

    @Inject
    private MemberSkillsServices memberSkillsService;

    /**
     * Create and save a new member skill.
     *
     * @param memberSkill, {@link MemberSkillCreateDTO}
     * @return {@link HttpResponse< MemberSkill >}
     */

    @Post(value = "/")
    public HttpResponse<MemberSkill> createAMemberSkill(@Body @Valid MemberSkillCreateDTO memberSkill) {
        MemberSkill newMemberSkill = memberSkillsService.save(new MemberSkill(memberSkill.getMemberid(), memberSkill.getSkillid()));

        if (newMemberSkill == null) {
            return HttpResponse.status(HttpStatus.valueOf(409), "already exists");
        } else {
            return HttpResponse
                    .created(newMemberSkill)
                    .headers(headers -> headers.location(location(newMemberSkill.getId())));
        }
    }

    protected URI location(UUID uuid) {
        return URI.create("/services/member-skill/" + uuid);
    }

}
