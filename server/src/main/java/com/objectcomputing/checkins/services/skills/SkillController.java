package com.objectcomputing.checkins.services.skills;

import com.objectcomputing.checkins.exceptions.NotFoundException;
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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/skills")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "skill")
public class SkillController {


    private final SkillServices skillServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public SkillController(SkillServices skillServices, EventLoopGroup eventLoopGroup, @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.skillServices = skillServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Create and save a new skill.
     *
     * @param skill, {@link SkillCreateDTO}
     * @return {@link HttpResponse< Skill >}
     */

    @Post()
    public Mono<HttpResponse<Skill>> createASkill(@Body @Valid SkillCreateDTO skill, HttpRequest<SkillCreateDTO> request) {

        return Mono.fromCallable(() -> skillServices.save(new Skill(skill.getName(), skill.isPending(), skill.getDescription(), skill.isExtraneous())))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(createdSkill -> {
                    return (HttpResponse<Skill>) HttpResponse.created(createdSkill)
                            .headers(headers -> headers.location(
                                    URI.create(String.format("%s/%s", request.getPath(), createdSkill.getId()))));
                }).subscribeOn(Schedulers.fromExecutor(ioExecutorService));

    }

    /**
     * Find and read a skill given its id.
     *
     * @param id {@link UUID} of the skill entry
     * @return
     */

    @Get("/{id}")
    public Mono<HttpResponse<Skill>> getById(@NotNull UUID id) {

        return Mono.fromCallable(() -> {
            Skill result = skillServices.readSkill(id);
            if (result == null) {
                throw new NotFoundException("No skill for UUID");
            }
            return result;
        }).publishOn(Schedulers.fromExecutor(eventLoopGroup)).map(skills -> {
            return (HttpResponse<Skill>) HttpResponse.ok(skills);
        }).subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Find and read a skill or skills given its name, or pending status, if both are blank get all skills.
     *
     * @param name,    name of the skill
     * @param pending, whether or not the skill has been officially accepted
     * @return {@link Set <Skill > list of Skills
     */

    @Get("/{?name,pending}")
    public Mono<HttpResponse<Set<Skill>>> findByValue(@Nullable String name,
                                                        @Nullable Boolean pending) {

        return Mono.fromCallable(() -> skillServices.findByValue(name, pending))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(skills -> (HttpResponse<Set<Skill>>) HttpResponse.ok(skills))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Update the pending status of a skill.
     *
     * @param skill, {@link Skill}
     * @return {@link HttpResponse<Skill>}
     */
    @Put()
    public Mono<HttpResponse<Skill>> update(@Body @Valid Skill skill, HttpRequest<Skill> request) {

        return Mono.fromCallable(() -> skillServices.update(skill))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(updatedSkill -> (HttpResponse<Skill>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedSkill.getId()))))
                        .body(updatedSkill))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
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