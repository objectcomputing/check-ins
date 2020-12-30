package com.objectcomputing.checkins.services.skills;

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

@Controller("/services/skill")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "skill")
public class SkillController {


    private final SkillServices skillServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public SkillController(SkillServices skillServices, EventLoopGroup eventLoopGroup,  @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.skillServices = skillServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

//    @Error(exception = SkillBadArgException.class)
//    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, SkillBadArgException e) {
//        JsonError error = new JsonError(e.getMessage())
//                .link(Link.SELF, Link.of(request.getUri()));
//
//        return HttpResponse.<JsonError>badRequest()
//                .body(error);
//    }

//    @Error(exception = SkillAlreadyExistsException.class)
//    public HttpResponse<?> handleAlreadyExists(HttpRequest<?> request, SkillAlreadyExistsException e) {
//        JsonError error = new JsonError(e.getMessage())
//                .link(Link.SELF, Link.of(request.getUri()));
//
//        return HttpResponse.<JsonError>status(HttpStatus.CONFLICT).body(error);
//    }

    @Error(exception = SkillNotFoundException.class)
    public HttpResponse<?> handleNotFound(HttpRequest<?> request, SkillNotFoundException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>notFound()
                .body(error);
    }

    /**
     * Create and save a new skill.
     *
     * @param skill, {@link SkillCreateDTO}
     * @return {@link HttpResponse< Skill >}
     */

    @Post()
    public Single<HttpResponse<Skill>> createASkill(@Body @Valid SkillCreateDTO skill, HttpRequest<SkillCreateDTO> request) {

        return Single.fromCallable(() -> skillServices.save(new Skill(skill.getName(),skill.isPending(),skill.getDescription(),skill.isExtraneous())))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(createdSkill -> {return (HttpResponse<Skill>) HttpResponse.created(createdSkill)
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), createdSkill.getId()))));}).subscribeOn(Schedulers.from(ioExecutorService));

    }

    /**
     * Find and read a skill given its id.
     *
     * @param id {@link UUID} of the skill entry
     * @return
     */

    @Get("/{id}")
    public Single<HttpResponse<Skill>> getById(@NotNull UUID id) {

        return Single.fromCallable(() -> {
            Skill result = skillServices.readSkill(id);
            if(result == null) {
                throw new SkillNotFoundException("No skill for UUID");
            } return result;
        }).observeOn(Schedulers.from(eventLoopGroup)).map(skills -> {
            return(HttpResponse<Skill>) HttpResponse.ok(skills);
        }).subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Find and read a skill or skills given its name, or pending status, if both are blank get all skills.
     *
     * @param name,    name of the skill
     * @param pending, whether or not the skill has been officially accepted
     * @return {@link Set < Skill > list of Skills}
     */

    @Get("/{?name,pending}")
    public Single<HttpResponse<Set<Skill>>> findByValue(@Nullable String name,
                                  @Nullable Boolean pending) {

        return Single.fromCallable(() -> skillServices.findByValue(name,pending))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(skills -> (HttpResponse<Set<Skill>>) HttpResponse.ok(skills))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Update the pending status of a skill.
     *
     * @param skill, {@link Skill}
     * @return {@link HttpResponse<Skill>}
     */
    @Put()
    public Single<HttpResponse<Skill>> update(@Body @Valid Skill skill, HttpRequest<Skill> request) {

        return Single.fromCallable(() -> skillServices.update(skill))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(updatedSkill -> (HttpResponse<Skill>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedSkill.getId()))))
                        .body(updatedSkill))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Delete A skill
     *
     * @param id, id of {@link Skill} to delete
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteSkill(@NotNull UUID id) {
        skillServices.delete(id);
        return HttpResponse
                .ok();
    }

}