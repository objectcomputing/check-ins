package com.objectcomputing.checkins.services.skillcategory.skillcategory_skill;

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
import java.net.URI;
import java.util.concurrent.ExecutorService;

@Controller("/services/skills/category-skills")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "skillcategory_skills")
public class SkillCategorySkillController {

    private final SkillCategorySkillServices skillCategorySkillServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public SkillCategorySkillController(SkillCategorySkillServices skillCategorySkillServices,
                                        EventLoopGroup eventLoopGroup,
                                        @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.skillCategorySkillServices = skillCategorySkillServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    @Post()
    @RequiredPermission(Permissions.CAN_EDIT_SKILL_CATEGORIES)
    public Mono<HttpResponse<SkillCategorySkill>> create(@Body @Valid SkillCategorySkillId dto,
                                                         HttpRequest<SkillCategorySkillId> request) {
        return Mono
                .fromCallable(() -> skillCategorySkillServices.save(dto))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(skillCategorySkill -> {
                    URI uri = URI.create(String.format("%s", request.getPath()));
                    return (HttpResponse<SkillCategorySkill>) HttpResponse
                            .created(skillCategorySkill)
                            .headers(headers -> headers.location(uri));
                })
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    @Delete()
    @RequiredPermission(Permissions.CAN_EDIT_SKILL_CATEGORIES)
    public Mono<HttpResponse<?>> delete(@Body @Valid SkillCategorySkillId dto) {
        return Mono.fromRunnable(() -> skillCategorySkillServices.delete(dto))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService)).thenReturn(HttpResponse.ok());
    }

}
