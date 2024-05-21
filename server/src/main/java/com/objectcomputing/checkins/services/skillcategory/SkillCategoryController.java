package com.objectcomputing.checkins.services.skillcategory;

import com.objectcomputing.checkins.exceptions.NotFoundException;
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
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Controller("/services/skills/categories")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "skillcategory")
public class SkillCategoryController {

    private final SkillCategoryServices skillCategoryServices;

    public SkillCategoryController(SkillCategoryServices skillCategoryServices) {
        this.skillCategoryServices = skillCategoryServices;
    }

    @Post()
    @RequiredPermission(Permission.CAN_EDIT_SKILL_CATEGORIES)
    public Mono<HttpResponse<SkillCategory>> create(@Body @Valid SkillCategoryCreateDTO dto, HttpRequest<?> request) {
        return Mono.fromCallable(() -> {
                    SkillCategory skillCategory = new SkillCategory(dto.getName(), dto.getDescription());
                    return skillCategoryServices.save(skillCategory);
                })
                .map(createdSkillCategory -> HttpResponse.created(createdSkillCategory)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdSkillCategory.getId())))));
    }

    @Put()
    @RequiredPermission(Permission.CAN_EDIT_SKILL_CATEGORIES)
    public Mono<HttpResponse<SkillCategory>> update(@Body @Valid SkillCategoryUpdateDTO dto, HttpRequest<?> request) {
        return Mono.fromCallable(() -> {
                    SkillCategory skillCategory = new SkillCategory(dto.getId(), dto.getName(), dto.getDescription());
                    return skillCategoryServices.update(skillCategory);
                })
                .map(skillCategory -> HttpResponse.ok(skillCategory)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), skillCategory.getId())))));
    }

    @Get("/{id}")
    @RequiredPermission(Permission.CAN_VIEW_SKILL_CATEGORIES)
    public Mono<HttpResponse<SkillCategoryResponseDTO>> getById(@NotNull UUID id) {
        return Mono.fromCallable(() -> {
            SkillCategoryResponseDTO result = skillCategoryServices.read(id);
            if (result == null) {
                throw new NotFoundException("No skill category for UUID");
            }
            return result;
        }).map(HttpResponse::ok);
    }

    @Get("/with-skills")
    @RequiredPermission(Permission.CAN_VIEW_SKILL_CATEGORIES)
    public Mono<HttpResponse<List<SkillCategoryResponseDTO>>> findAllWithSkills() {
        return Mono.fromCallable(skillCategoryServices::findAllWithSkills)
                .map(HttpResponse::ok);
    }

    @Delete("/{id}")
    @RequiredPermission(Permission.CAN_EDIT_SKILL_CATEGORIES)
    public Mono<HttpResponse<?>> delete(@NotNull UUID id) {
        return Mono.fromRunnable(() -> skillCategoryServices.delete(id))
                .thenReturn(HttpResponse.ok());
    }

}
