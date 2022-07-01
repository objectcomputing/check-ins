package com.objectcomputing.checkins.services.member_skill;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.skills.Skill;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/member-skills")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "member-skills")
public class MemberSkillController {

    private final MemberSkillServices memberSkillsService;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public MemberSkillController(MemberSkillServices memberSkillServices,
                                 EventLoopGroup eventLoopGroup,
                                 @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.memberSkillsService = memberSkillServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }


    /**
     * Create and save a new member skill.
     *
     * @param memberSkill, {@link MemberSkillCreateDTO}
     * @return {@link HttpResponse< MemberSkill >}
     */
    @Post()
    public Mono<HttpResponse<MemberSkill>> createAMemberSkill(@Body @Valid @NotNull MemberSkillCreateDTO memberSkill, HttpRequest<MemberSkillCreateDTO> request) {

        return Mono.fromCallable(() -> memberSkillsService.save(new MemberSkill(memberSkill.getMemberid(),
                memberSkill.getSkillid(), memberSkill.getSkilllevel(), memberSkill.getLastuseddate())))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(createdMemberSkill -> (HttpResponse<MemberSkill>)HttpResponse
                        .created(createdMemberSkill)
                        .headers(headers -> headers.location(
                            URI.create(String.format("%s/%s", request.getPath(), createdMemberSkill.getId())))))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Delete Member skill
     *
     * @param id, id of {@link MemberSkill} to delete
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteMemberSkill(@NotNull UUID id) {
        memberSkillsService.delete(id);
        return HttpResponse
                .ok();
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
        })
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(memberSkill -> (HttpResponse<MemberSkill>)HttpResponse.ok(memberSkill))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Find member skills that match all filled in parameters, return all results when given no params
     *
     * @param memberid {@link UUID} of member profile
     * @param skillid  {@link UUID} of skills
     * @return {@link List <MemberSkill > list of Member Skills
     */
    @Get("/{?memberid,skillid}")
    public Mono<HttpResponse<Set<MemberSkill>>> findMemberSkills(@Nullable UUID memberid,
                                             @Nullable UUID skillid) {
        return Mono.fromCallable(() -> memberSkillsService.findByFields(memberid, skillid))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(memberSkills -> (HttpResponse<Set<MemberSkill>>)HttpResponse
                        .ok(memberSkills)).subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }


    /**
     * Update a MemberSkill
     *
     * @param memberSkill, {@link MemberSkill}
     * @return {@link MemberSkill}
     */
    @Put()
    public Mono<HttpResponse<MemberSkill>> update(@Body @Valid MemberSkill memberSkill, HttpRequest<Skill> request) {

        return Mono.fromCallable(() -> memberSkillsService.update(memberSkill))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(updatedMemberSkill -> (HttpResponse<MemberSkill>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedMemberSkill.getId()))))
                        .body(updatedMemberSkill))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }
}
