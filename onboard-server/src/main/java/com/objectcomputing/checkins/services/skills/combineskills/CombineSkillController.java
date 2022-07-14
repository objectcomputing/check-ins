package com.objectcomputing.checkins.services.skills.combineskills;

import com.objectcomputing.checkins.services.skills.Skill;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import java.net.URI;
import java.util.concurrent.ExecutorService;

@Controller("/services/skills/combine")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "combineskill")
public class CombineSkillController {

    private final CombineSkillServices combineSkillServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public CombineSkillController(CombineSkillServices combineSkillServices,
                                  EventLoopGroup eventLoopGroup,
                                  @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.combineSkillServices = combineSkillServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Create and save a new skill from similar skills found.
     *
     * @param skill, {@link CombineSkillsDTO}
     * @return {@link HttpResponse<Skill>}
     */

    @Post()
    public Mono<HttpResponse<Skill>> createNewSkillFromList(@Body @Valid CombineSkillsDTO skill, HttpRequest<CombineSkillsDTO> request) {
        return Mono.fromCallable(() -> combineSkillServices.combine(skill))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(createdSkill -> (HttpResponse<Skill>) HttpResponse.created(createdSkill)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdSkill.getId())))))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

}
