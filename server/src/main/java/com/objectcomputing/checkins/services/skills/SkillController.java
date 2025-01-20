package com.objectcomputing.checkins.services.skills;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@Controller("/services/skills")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
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
    @Post
    public HttpResponse<Skill> createASkill(@Body @Valid SkillCreateDTO skill, HttpRequest<?> request) {
        Skill createdSkill = skillServices.save(new Skill(skill.getName(), skill.isPending(), skill.getDescription(), skill.isExtraneous()));
        return HttpResponse.created(createdSkill)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdSkill.getId()))));

    }

    /**
     * Find and read a skill given its id.
     *
     * @param id {@link UUID} of the skill entry
     * @return
     */
    @Get("/{id}")
    public Skill getById(@NotNull UUID id) {
        Skill result = skillServices.readSkill(id);
        if (result == null) {
            throw new NotFoundException("No skill for UUID");
        }
        return result;
    }

    /**
     * Find and read a skill or skills given its name, or pending status, if both are blank get all skills.
     *
     * @param name,    name of the skill
     * @param pending, whether the skill has been officially accepted
     * @return {@link Set <Skill > list of Skills
     */
    @Get("/{?name,pending}")
    public Set<Skill> findByValue(@Nullable String name, @Nullable Boolean pending) {
        return skillServices.findByValue(name, pending);
    }

    /**
     * Update the pending status of a skill.
     *
     * @param skill, {@link Skill}
     * @return {@link HttpResponse<Skill>}
     */
    @Put
    @RequiredPermission(Permission.CAN_EDIT_SKILLS)
    public HttpResponse<Skill> update(@Body @Valid Skill skill, HttpRequest<?> request) {
        Skill updatedSkill = skillServices.update(skill);
        return HttpResponse.ok(updatedSkill)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedSkill.getId()))));
    }

    /**
     * Delete A skill
     *
     * @param id, id of {@link Skill} to delete
     */
    @Delete("/{id}")
    @Status(HttpStatus.OK)
    @RequiredPermission(Permission.CAN_EDIT_SKILLS)
    public void deleteSkill(@NotNull UUID id) {
        skillServices.delete(id);
    }
}
