package com.objectcomputing.checkins.services.skillcategory;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.constraints.NotNull;
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

    public SkillCategoryController(SkillCategoryServices skillCategoryServices, EventLoopGroup eventLoopGroup, @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.skillCategoryServices = skillCategoryServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    @Get("/{id}")
    public Mono<HttpResponse<SkillCategory>> getById(@NotNull UUID id) {
        return Mono.fromCallable(() -> {
            SkillCategory result = skillCategoryServices.read(id);
            if (result == null) {
                throw new NotFoundException("No skill category for UUID");
            }
            return result;
        }).publishOn(Schedulers.fromExecutor(eventLoopGroup)).map(skills -> {
            return (HttpResponse<SkillCategory>) HttpResponse.ok(skills);
        }).subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

}
