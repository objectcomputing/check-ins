package com.objectcomputing.checkins.services.skills;

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
import java.util.Set;
import java.util.UUID;

@Controller("/services/skills")
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "skill")
public class SkillController {

    private final SkillServices skillServices;

    public SkillController(SkillServices skillServices) {
        this.skillServices = skillServices;
    }

    /**
     * Create and save a new skill.
     *
     * @param skill, {@link SkillCreateDTO}
     * @return {@link HttpResponse< Skill >}
     */

    @Post()
    public Mono<HttpResponse<Skill>> createASkill(@Body @Valid SkillCreateDTO skill, HttpRequest<?> request) {
        return Mono.fromCallable(() -> skillServices.save(new Skill(skill.getName(), skill.isPending(),
                        skill.getDescription(), skill.isExtraneous())))
                .map(createdSkill -> HttpResponse.created(createdSkill)
                        .headers(headers -> headers.location(
                                URI.create(String.format("%s/%s", request.getPath(), createdSkill.getId())))));

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
        }).map(HttpResponse::ok);
    }

    /**
     * Find and read a skill or skills given its name, or pending status, if both are blank get all skills.
     *
     * @param name,    name of the skill
     * @param pending, whether the skill has been officially accepted
     * @return {@link Set <Skill > list of Skills
     */

    @Get("/{?name,pending}")
    public Mono<HttpResponse<Set<Skill>>> findByValue(@Nullable String name, @Nullable Boolean pending) {
        return Mono.fromCallable(() -> skillServices.findByValue(name, pending))
                .map(HttpResponse::ok);
    }

    /**
     * Update the pending status of a skill.
     *
     * @param skill, {@link Skill}
     * @return {@link HttpResponse<Skill>}
     */
    @Put()
    public Mono<HttpResponse<Skill>> update(@Body @Valid Skill skill, HttpRequest<?> request) {
        return Mono.fromCallable(() -> skillServices.update(skill))
                .map(updatedSkill -> HttpResponse.ok(updatedSkill)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedSkill.getId())))));
    }

    /**
     * Delete A skill
     *
     * @param id, id of {@link Skill} to delete
     */
    @Delete("/{id}")
    public Mono<HttpResponse<?>> deleteSkill(@NotNull UUID id) {
        return Mono.fromRunnable(() -> skillServices.delete(id))
                .thenReturn(HttpResponse.ok());
    }

}