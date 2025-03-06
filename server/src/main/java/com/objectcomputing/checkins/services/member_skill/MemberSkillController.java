package com.objectcomputing.checkins.services.member_skill;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@Controller("/services/member-skills")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "member-skills")
public class MemberSkillController {

    private final MemberSkillServices memberSkillsService;

    public MemberSkillController(MemberSkillServices memberSkillServices) {
        this.memberSkillsService = memberSkillServices;
    }

    /**
     * Create and save a new member skill.
     *
     * @param memberSkill, {@link MemberSkillCreateDTO}
     * @return {@link HttpResponse< MemberSkill >}
     */
    @Post
    public HttpResponse<MemberSkill> createAMemberSkill(@Body @Valid @NotNull MemberSkillCreateDTO memberSkill, HttpRequest<?> request) {
        MemberSkill createdMemberSkill = memberSkillsService.save(
            new MemberSkill(memberSkill.getMemberid(),
                            memberSkill.getSkillid(),
                            memberSkill.getSkilllevel(),
                            memberSkill.getLastuseddate(),
                            memberSkill.isInterested())
        );
        return HttpResponse.created(createdMemberSkill)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdMemberSkill.getId()))));
    }

    /**
     * Delete Member skill
     *
     * @param id, id of {@link MemberSkill} to delete
     */
    @Delete("/{id}")
    @Status(HttpStatus.OK)
    public void deleteMemberSkill(@NotNull UUID id) {
        memberSkillsService.delete(id);
    }

    /**
     * Get MemberSkill based off id
     *
     * @param id {@link UUID} of the member skill entry
     * @return {@link MemberSkill}
     */
    @Get("/{id}")
    public MemberSkill readMemberSkill(@NotNull UUID id) {
        MemberSkill result = memberSkillsService.read(id);
        if (result == null) {
            throw new NotFoundException("No member skill for UUID");
        }
        return result;
    }

    /**
     * Find member skills that match all filled in parameters, return all results when given no params
     *
     * @param memberid {@link UUID} of member profile
     * @param skillid  {@link UUID} of skills
     * @return set of Member Skills
     */
    @Get("/{?memberid,skillid}")
    public Set<MemberSkill> findMemberSkills(@Nullable UUID memberid, @Nullable UUID skillid) {
        return memberSkillsService.findByFields(memberid, skillid);
    }

    /**
     * Update a MemberSkill
     *
     * @param memberSkill, {@link MemberSkill}
     * @return {@link MemberSkill}
     */
    @Put
    public HttpResponse<MemberSkill> update(@Body @Valid MemberSkill memberSkill, HttpRequest<?> request) {
        MemberSkill updatedMemberSkill = memberSkillsService.update(memberSkill);
        return HttpResponse.ok(updatedMemberSkill)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedMemberSkill.getId()))));
    }
}
