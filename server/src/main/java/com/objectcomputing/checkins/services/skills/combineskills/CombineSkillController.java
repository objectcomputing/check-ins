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
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import java.net.URI;

@Controller("/services/skills/combine")
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "combineskill")
public class CombineSkillController {

    private final CombineSkillServices combineSkillServices;

    public CombineSkillController(CombineSkillServices combineSkillServices) {
        this.combineSkillServices = combineSkillServices;
    }

    /**
     * Create and save a new skill from similar skills found.
     *
     * @param skill, {@link CombineSkillsDTO}
     * @return {@link HttpResponse<Skill>}
     */

    @Post()
    public Mono<HttpResponse<Skill>> createNewSkillFromList(@Body @Valid CombineSkillsDTO skill, HttpRequest<?> request) {
        return Mono.fromCallable(() -> combineSkillServices.combine(skill))
                .map(createdSkill -> HttpResponse.created(createdSkill)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdSkill.getId())))));
    }

}
