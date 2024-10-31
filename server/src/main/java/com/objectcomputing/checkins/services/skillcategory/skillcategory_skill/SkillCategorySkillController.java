package com.objectcomputing.checkins.services.skillcategory.skillcategory_skill;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.net.URI;

@Controller("/services/skills/category-skills")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "skillcategory_skills")
public class SkillCategorySkillController {

    private final SkillCategorySkillServices skillCategorySkillServices;

    public SkillCategorySkillController(SkillCategorySkillServices skillCategorySkillServices) {
        this.skillCategorySkillServices = skillCategorySkillServices;
    }

    @Post
    @RequiredPermission(Permission.CAN_EDIT_SKILL_CATEGORIES)
    public HttpResponse<SkillCategorySkill> create(@Body @Valid SkillCategorySkillId dto, HttpRequest<?> request) {
        SkillCategorySkill skillCategorySkill = skillCategorySkillServices.save(dto);
        return HttpResponse.created(skillCategorySkill)
                        .headers(headers -> headers.location(URI.create(String.format("%s", request.getPath()))));
    }

    @Delete
    @RequiredPermission(Permission.CAN_EDIT_SKILL_CATEGORIES)
    @Status(HttpStatus.OK)
    public void delete(@Body @Valid SkillCategorySkillId dto) {
        skillCategorySkillServices.delete(dto);
    }
}
