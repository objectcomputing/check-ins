package com.objectcomputing.checkins.services.skills.tags;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Tag(name = "skilltags")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Controller("/services/skillTags")
public class SkillTagController {
    private final SkillTagService skillTagService;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public SkillTagController(SkillTagService skillTagService,
                              EventLoopGroup eventLoopGroup,
                              @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.skillTagService = skillTagService;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    @Error(exception = SkillTagNotFoundException.class)
    public HttpResponse<?> handleNotFound(HttpRequest<?> request, SkillTagNotFoundException stnfe) {
        JsonError error = new JsonError(stnfe.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>status(HttpStatus.NOT_FOUND).body(error);
    }

    @Get("/{id}")
    public Single<HttpResponse<SkillTagResponseDTO>> getById(@NotNull UUID id) {
        return Single.fromCallable(() -> skillTagService.findById(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(skillTagResponseDTO -> (HttpResponse<SkillTagResponseDTO>)HttpResponse
                        .ok(skillTagResponseDTO))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    @Get("/{?name,skillId}")
    public Single<HttpResponse<List<SkillTagResponseDTO>>> findTags(@Nullable String name, @Nullable UUID skillId) {
        return Single.fromCallable(() -> skillTagService.search(name, skillId))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(skillTagResponseDTOs -> (HttpResponse<List<SkillTagResponseDTO>>)HttpResponse
                        .ok(skillTagResponseDTOs))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    @Post("/")
    public Single<HttpResponse<SkillTagResponseDTO>> save(@Valid @NotNull SkillTagCreateDTO dto) {
        return Single.fromCallable(() -> skillTagService.save(dto))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(skillTagResponseDTO -> (HttpResponse<SkillTagResponseDTO>)HttpResponse
                        .ok(skillTagResponseDTO))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    @Put("/")
    public Single<HttpResponse<SkillTagResponseDTO>> update(@Valid SkillTagUpdateDTO dto) {
        return Single.fromCallable(() -> skillTagService.update(dto))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(skillTagResponseDTO -> (HttpResponse<SkillTagResponseDTO>)HttpResponse
                        .ok(skillTagResponseDTO))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }
}
