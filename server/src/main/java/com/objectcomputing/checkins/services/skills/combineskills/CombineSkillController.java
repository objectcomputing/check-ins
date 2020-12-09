package com.objectcomputing.checkins.services.skills.combineskills;

import com.objectcomputing.checkins.services.skills.Skill;
import com.objectcomputing.checkins.services.skills.SkillServices;
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
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Named;
import javax.validation.Valid;
import java.net.URI;
import java.util.concurrent.ExecutorService;

@Controller("/services/skill/combine")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "combineskill")
public class CombineSkillController {

    private final CombineSkillServices combineSkillServices;
    private final SkillServices skillServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public CombineSkillController(CombineSkillServices combineSkillServices, SkillServices skillServices, EventLoopGroup eventLoopGroup, @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.combineSkillServices = combineSkillServices;
        this.skillServices = skillServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Create and save a new skill from similar skills found.
     *
     * @param skill, {@link CombineSkillsDTO}
     * @return {@link HttpResponse< Skill >}
     */

    @Post(value = "/")
    public Single<HttpResponse<Skill>> createNewSkillFromList(@Body @Valid CombineSkillsDTO skill, HttpRequest<CombineSkillsDTO> request) {

        return Single.fromCallable(() -> combineSkillServices.save(skill))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(createdSkill -> {return (HttpResponse<Skill>) HttpResponse.created(createdSkill)
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), createdSkill.getId()))));}).subscribeOn(Schedulers.from(ioExecutorService));

    }

}