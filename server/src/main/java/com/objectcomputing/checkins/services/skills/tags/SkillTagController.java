package com.objectcomputing.checkins.services.skills.tags;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
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

@Tag(name = "services/skilltags")
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

    @Post
    public Single<HttpResponse<SkillTagResponseDTO>> save(@Valid SkillTagCreateDTO dto) {
        return Single.fromCallable(() -> skillTagService.save(dto))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(skillTagResponseDTO -> (HttpResponse<SkillTagResponseDTO>)HttpResponse
                        .ok(skillTagResponseDTO))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    @Put
    public Single<HttpResponse<SkillTagResponseDTO>> update(@Valid SkillTagUpdateDTO dto) {
        return Single.fromCallable(() -> skillTagService.update(dto))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(skillTagResponseDTO -> (HttpResponse<SkillTagResponseDTO>)HttpResponse
                        .ok(skillTagResponseDTO))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }
}
