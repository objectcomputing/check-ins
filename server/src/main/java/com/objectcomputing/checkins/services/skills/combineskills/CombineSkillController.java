package com.objectcomputing.checkins.services.skills.combineskills;

import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.skills.*;
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
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/skill/combine")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "combineskill")
public class CombineSkillController {

    private final SkillServices skillServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public CombineSkillController(SkillServices skillServices, EventLoopGroup eventLoopGroup, @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.skillServices = skillServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    @Error(exception = SkillBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, SkillBadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    @Error(exception = SkillAlreadyExistsException.class)
    public HttpResponse<?> handleAlreadyExists(HttpRequest<?> request, SkillAlreadyExistsException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>status(HttpStatus.CONFLICT).body(error);
    }

    @Error(exception = SkillNotFoundException.class)
    public HttpResponse<?> handleNotFound(HttpRequest<?> request, SkillNotFoundException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>notFound()
                .body(error);
    }

    /**
     * Create and save a new skill from similar skills found.
     *
     * @param skill, {@link CombineSkillsDTO}
     * @return {@link HttpResponse< Skill >}
     */

    @Post(value = "/")
    public Single<HttpResponse<Skill>> createNewSkillFromList(@Body @Valid CombineSkillsDTO skill, HttpRequest<CombineSkillsDTO> request) {

        // not skill services - combine skill services

        return Single.fromCallable(() -> skillServices.save(new Skill(skill.getName(),true,skill.getDescription(),false)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(createdSkill -> {return (HttpResponse<Skill>) HttpResponse.created(createdSkill)
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), createdSkill.getId()))));}).subscribeOn(Schedulers.from(ioExecutorService));

    }
//        return Single.fromCallable(() -> skillServices.save(new Skill(skill.getName(),true,skill.getDescription(),skill.isExtraneous())))
//                .observeOn(Schedulers.from(eventLoopGroup))
//                .map(createdSkill -> {return (HttpResponse<Skill>) HttpResponse.created(createdSkill)
//                .headers(headers -> headers.location(
//                        URI.create(String.format("%s/%s", request.getPath(), createdSkill.getId()))));}).subscribeOn(Schedulers.from(ioExecutorService));
//
//    }

}