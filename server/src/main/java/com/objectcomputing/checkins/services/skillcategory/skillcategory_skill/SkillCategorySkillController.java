package com.objectcomputing.checkins.services.skillcategory.skillcategory_skill;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
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
import reactor.core.publisher.Mono;

import java.net.URI;

@Controller("/services/skills/category-skills")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "skillcategory_skills")
public class SkillCategorySkillController {

    private final SkillCategorySkillServices skillCategorySkillServices;

    public SkillCategorySkillController(SkillCategorySkillServices skillCategorySkillServices) {
        this.skillCategorySkillServices = skillCategorySkillServices;
    }

    @Post()
    @RequiredPermission(Permission.CAN_EDIT_SKILL_CATEGORIES)
    public Mono<HttpResponse<SkillCategorySkill>> create(@Body @Valid SkillCategorySkillId dto, HttpRequest<?> request) {
        return Mono.fromCallable(() -> skillCategorySkillServices.save(dto))
                .map(skillCategorySkill -> HttpResponse.created(skillCategorySkill)
                        .headers(headers -> headers.location(URI.create(String.format("%s", request.getPath())))));
    }

    @Delete()
    @RequiredPermission(Permission.CAN_EDIT_SKILL_CATEGORIES)
    public Mono<HttpResponse<?>> delete(@Body @Valid SkillCategorySkillId dto) {
        return Mono.fromRunnable(() -> skillCategorySkillServices.delete(dto))
                .thenReturn(HttpResponse.ok());
    }

}
