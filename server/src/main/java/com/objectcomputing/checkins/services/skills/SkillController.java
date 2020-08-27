package com.objectcomputing.checkins.services.skills;

import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller("/services/skill")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "skill")
public class SkillController {

    @Inject
    private SkillServices skillServices;

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

    /**
     * Create and save a new skill.
     *
     * @param skill, {@link SkillCreateDTO}
     * @return {@link HttpResponse< Skill >}
     */

    @Post(value = "/")
    public HttpResponse<Skill> createASkill(@Body @Valid SkillCreateDTO skill, HttpRequest<SkillCreateDTO> request) {
        Skill newSkill = skillServices.save(new Skill(skill.getName(), skill.isPending()));

        return HttpResponse
                .created(newSkill)
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), newSkill.getId()))));

    }

    /**
     * Find and read a skill given its id.
     *
     * @param id {@link UUID} of the skill entry
     * @return
     */

    @Get("/{id}")
    public Skill getById(@NotNull UUID id) {

        return skillServices.readSkill(id);

    }

    /**
     * Find and read a skill or skills given its name, or pending status, if both are blank get all skills.
     *
     * @param name,    name of the skill
     * @param pending, whether or not the skill has been officially accepted
     * @return {@link List < Skill > list of Skills}
     */

    @Get("/{?name,pending}")
    public Set<Skill> findByValue(@Nullable String name,
                                  @Nullable Boolean pending) {

        return skillServices.findByValue(name, pending);

    }

    /**
     * Update the pending status of a skill.
     *
     * @param skill, {@link Skill}
     * @return {@link HttpResponse< Skill >}
     */
    @Put("/")
    public HttpResponse<?> update(@Body @Valid Skill skill, HttpRequest<Skill> request) {

        Skill updatedSkill = skillServices.update(skill);
        return HttpResponse
                .ok()
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getUri(), skill.getId()))))
                .body(updatedSkill);

    }

    /**
     * Delete A skill
     *
     * @param id, id of {@link Skill} to delete
     */
    @Delete("/{id}")
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public HttpResponse<?> deleteSkill(@NotNull UUID id) {
        skillServices.delete(id);
        return HttpResponse
                .ok();
    }

}