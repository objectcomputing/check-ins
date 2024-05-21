package com.objectcomputing.checkins.services.member_skill;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller("/services/member-skills")
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
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
    public Mono<HttpResponse<MemberSkill>> createAMemberSkill(@Body @Valid @NotNull MemberSkillCreateDTO memberSkill, HttpRequest<?> request) {
        return Mono.fromCallable(() -> memberSkillsService.save(new MemberSkill(memberSkill.getMemberid(),
                memberSkill.getSkillid(), memberSkill.getSkilllevel(), memberSkill.getLastuseddate())))
                .map(createdMemberSkill -> HttpResponse.created(createdMemberSkill)
                        .headers(headers -> headers.location(
                            URI.create(String.format("%s/%s", request.getPath(), createdMemberSkill.getId())))));
    }

    /**
     * Delete Member skill
     *
     * @param id, id of {@link MemberSkill} to delete
     */
    @Delete("/{id}")
    public Mono<HttpResponse<?>> deleteMemberSkill(@NotNull UUID id) {
        return Mono.fromRunnable(() -> memberSkillsService.delete(id))
                .thenReturn(HttpResponse.ok());
    }

    /**
     * Get MemberSkill based off id
     *
     * @param id {@link UUID} of the member skill entry
     * @return {@link MemberSkill}
     */
    @Get("/{id}")
    public Mono<HttpResponse<MemberSkill>> readMemberSkill(@NotNull UUID id) {
        return Mono.fromCallable(() -> {
            MemberSkill result = memberSkillsService.read(id);
            if (result == null) {
                throw new NotFoundException("No member skill for UUID");
            }
            return result;
        }).map(HttpResponse::ok);
    }

    /**
     * Find member skills that match all filled in parameters, return all results when given no params
     *
     * @param memberid {@link UUID} of member profile
     * @param skillid  {@link UUID} of skills
     * @return {@link List <MemberSkill > list of Member Skills
     */
    @Get("/{?memberid,skillid}")
    public Mono<HttpResponse<Set<MemberSkill>>> findMemberSkills(@Nullable UUID memberid, @Nullable UUID skillid) {
        return Mono.fromCallable(() -> memberSkillsService.findByFields(memberid, skillid))
                .map(HttpResponse::ok);
    }


    /**
     * Update a MemberSkill
     *
     * @param memberSkill, {@link MemberSkill}
     * @return {@link MemberSkill}
     */
    @Put()
    public Mono<HttpResponse<MemberSkill>> update(@Body @Valid MemberSkill memberSkill, HttpRequest<?> request) {
        return Mono.fromCallable(() -> memberSkillsService.update(memberSkill))
                .map(updatedMemberSkill -> HttpResponse.ok(updatedMemberSkill)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedMemberSkill.getId())))));
    }
}
