package com.objectcomputing.checkins.services.skillcategory;

import com.objectcomputing.checkins.exceptions.NotFoundException;
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
import java.util.List;
import java.util.UUID;

@Controller("/services/skills/categories")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "skillcategory")
public class SkillCategoryController {

    private final SkillCategoryServices skillCategoryServices;

    public SkillCategoryController(SkillCategoryServices skillCategoryServices) {
        this.skillCategoryServices = skillCategoryServices;
    }

    @Post
    public HttpResponse<SkillCategory> create(@Body @Valid SkillCategoryCreateDTO dto, HttpRequest<?> request) {
        SkillCategory skillCategory = new SkillCategory(dto.getName(), dto.getDescription());
        SkillCategory createdSkillCategory = skillCategoryServices.save(skillCategory);
        return HttpResponse.created(createdSkillCategory)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdSkillCategory.getId()))));
    }

    @Put
    public HttpResponse<SkillCategory> update(@Body @Valid SkillCategoryUpdateDTO dto, HttpRequest<?> request) {
        SkillCategory skillCategory = new SkillCategory(dto.getId(), dto.getName(), dto.getDescription());
        SkillCategory update = skillCategoryServices.update(skillCategory);
        return HttpResponse.ok(update)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), update.getId()))));
    }

    @Get("/{id}")
    public SkillCategoryResponseDTO getById(@NotNull UUID id) {
        SkillCategoryResponseDTO result = skillCategoryServices.read(id);
        if (result == null) {
            throw new NotFoundException("No skill category for UUID");
        }
        return result;
    }

    @Get("/with-skills")
    public List<SkillCategoryResponseDTO> findAllWithSkills() {
        return skillCategoryServices.findAllWithSkills();
    }

    @Delete("/{id}")
    @Status(HttpStatus.OK)
    public void delete(@NotNull UUID id) {
        skillCategoryServices.delete(id);
    }
}
