package com.objectcomputing.checkins.services.skillcategory;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.security.permissions.Permissions;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/skills/categories")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "skillcategory")
public class SkillCategoryController {

    private final SkillCategoryServices skillCategoryServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public SkillCategoryController(SkillCategoryServices skillCategoryServices, EventLoopGroup eventLoopGroup,
                                   @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.skillCategoryServices = skillCategoryServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    @Post()
    @RequiredPermission(Permissions.CAN_EDIT_SKILL_CATEGORIES)
    public Mono<HttpResponse<SkillCategory>> create(@Body @Valid SkillCategoryCreateDTO dto, HttpRequest<SkillCategoryCreateDTO> request) {
        return Mono
                .fromCallable(() -> {
                    SkillCategory skillCategory = new SkillCategory(dto.getName(), dto.getDescription());
                    return skillCategoryServices.save(skillCategory);
                })
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(createdSkillCategory -> {
                    URI uri = URI.create(String.format("%s/%s", request.getPath(), createdSkillCategory.getId()));
                    return (HttpResponse<SkillCategory>) HttpResponse
                        .created(createdSkillCategory)
                        .headers(headers -> headers.location(uri));
                })
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    @Put()
    @RequiredPermission(Permissions.CAN_EDIT_SKILL_CATEGORIES)
    public Mono<HttpResponse<SkillCategory>> update(@Body @Valid SkillCategoryUpdateDTO dto, HttpRequest<SkillCategoryCreateDTO> request) {
        return Mono
                .fromCallable(() -> {
                    SkillCategory skillCategory = new SkillCategory(dto.getId(), dto.getName(), dto.getDescription());
                    return skillCategoryServices.update(skillCategory);
                })
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(skillCategory -> {
                    URI uri = URI.create(String.format("%s/%s", request.getPath(), skillCategory.getId()));
                    return (HttpResponse<SkillCategory>) HttpResponse
                            .ok(skillCategory)
                            .headers(headers -> headers.location(uri));
                })
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    @Get("/{id}")
    @RequiredPermission(Permissions.CAN_VIEW_SKILL_CATEGORIES)
    public Mono<HttpResponse<SkillCategoryResponseDTO>> getById(@NotNull UUID id) {
        return Mono.fromCallable(() -> {
            SkillCategoryResponseDTO result = skillCategoryServices.read(id);
            if (result == null) {
                throw new NotFoundException("No skill category for UUID");
            }
            return result;
        }).publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(skills -> (HttpResponse<SkillCategoryResponseDTO>) HttpResponse.ok(skills))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    @Get("/with-skills")
    @RequiredPermission(Permissions.CAN_VIEW_SKILL_CATEGORIES)
    public Mono<HttpResponse<List<SkillCategoryResponseDTO>>> findAllWithSkills() {
        return Mono.fromCallable(skillCategoryServices::findAllWithSkills)
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(skillCategoryResponseDTOS -> (HttpResponse<List<SkillCategoryResponseDTO>>) HttpResponse.ok(skillCategoryResponseDTOS))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    @Delete("/{id}")
    @RequiredPermission(Permissions.CAN_EDIT_SKILL_CATEGORIES)
    public Mono<HttpResponse<?>> delete(@NotNull UUID id) {
        return Mono.fromRunnable(() -> skillCategoryServices.delete(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService))
                .thenReturn(HttpResponse.ok());
    }

}
