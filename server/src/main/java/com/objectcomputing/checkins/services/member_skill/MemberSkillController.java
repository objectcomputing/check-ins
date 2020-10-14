package com.objectcomputing.checkins.services.member_skill;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/member-skill")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "member-skill")
public class MemberSkillController {

    @Inject
    private MemberSkillServices memberSkillsService;
    private EventLoopGroup eventLoopGroup;
    private ExecutorService ioExecutorService;

    public MemberSkillController(MemberSkillServices memberSkillServices,
                                 EventLoopGroup eventLoopGroup, @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.memberSkillsService = memberSkillServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    @Error(exception = MemberSkillBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, MemberSkillBadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    @Error(exception = MemberSkillAlreadyExistsException.class)
    public HttpResponse<?> handleAlreadyExists(HttpRequest<?> request, MemberSkillAlreadyExistsException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>status(HttpStatus.CONFLICT).body(error);
    }

    @Error(exception = MemberSkillNotFoundException.class)
    public HttpResponse<?> handleNotFound(HttpRequest<?> request, MemberSkillNotFoundException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>notFound()
                .body(error);
    }

    @Error(exception = CompositeException.class)
    public HttpResponse<?> handleRxException(HttpRequest<?> request, CompositeException e) {

        for (Throwable t : e.getExceptions()) {
            if (t instanceof MemberSkillBadArgException) {
                return handleBadArgs(request, (MemberSkillBadArgException) t);
            }
            else if (t instanceof MemberSkillNotFoundException) {
                return handleNotFound(request, (MemberSkillNotFoundException) t);
            }
            else if (t instanceof MemberSkillAlreadyExistsException) {
                return handleAlreadyExists(request, (MemberSkillAlreadyExistsException) t);
            }
        }

        return HttpResponse.<JsonError>serverError();
    }

    /**
     * Create and save a new member skill.
     *
     * @param memberSkill, {@link MemberSkillCreateDTO}
     * @return {@link HttpResponse< MemberSkill >}
     */

    @Post(value = "/")
    public Single<HttpResponse<MemberSkill>> createAMemberSkill(@Body @Valid @NotNull MemberSkillCreateDTO memberSkill, HttpRequest<MemberSkillCreateDTO> request) {

        return Single.fromCallable(() -> memberSkillsService.save(new MemberSkill(memberSkill.getMemberid(),
                memberSkill.getSkillid())))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(createdMemberSkill -> (HttpResponse<MemberSkill>)HttpResponse
                        .created(createdMemberSkill)
                        .headers(headers -> headers.location(
                            URI.create(String.format("%s/%s", request.getPath(), createdMemberSkill.getId()))))).subscribeOn(Schedulers.from(ioExecutorService));
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
    public Single<HttpResponse<MemberSkill>> readMemberSkill(@NotNull UUID id) {

        return Single.fromCallable(() -> {
            MemberSkill result = memberSkillsService.read(id);
            if (result == null) {
                throw new MemberSkillNotFoundException("No member skill for UUID");
            }
            return result;
        })
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(memberSkill -> (HttpResponse<MemberSkill>)HttpResponse.ok(memberSkill))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Find member skills that match all filled in parameters, return all results when given no params
     *
     * @param memberid {@link UUID} of member profile
     * @param skillid  {@link UUID} of skills
     * @return {@link List < MemberSkill > list of Member Skills}
     */
    @Get("/{?memberid,skillid}")
    public Single<HttpResponse<Set<MemberSkill>>> findMemberSkills(@Nullable UUID memberid,
                                             @Nullable UUID skillid) {
        return Single.fromCallable(() -> memberSkillsService.findByFields(memberid, skillid))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(memberSkills -> (HttpResponse<Set<MemberSkill>>)HttpResponse
                        .ok(memberSkills)).subscribeOn(Schedulers.from(ioExecutorService));
    }

}
