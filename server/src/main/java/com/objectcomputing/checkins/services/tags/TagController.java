package com.objectcomputing.checkins.services.tags;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import jakarta.inject.Named;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;


@Controller("/services/tags")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@io.swagger.v3.oas.annotations.tags.Tag(name = "tags")

public class TagController {

    private final TagServices tagServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public TagController(TagServices tagServices,
                         EventLoopGroup eventLoopGroup,
                         @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.tagServices = tagServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
        }

    /**
     * Create and save a new tag.
     *
     * @param tag, {@link TagCreateDTO}
     * @return {@link HttpResponse<  Tag  >}
     */
    @Post()
    public Mono<HttpResponse<Tag>> createTag(@Body @Valid @NotNull TagCreateDTO tag, HttpRequest<?> request) {

        return Mono.fromCallable(() -> tagServices.save(new Tag(tag.getName())))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(createdTag -> (HttpResponse<Tag>)HttpResponse
                        .created(createdTag)
                        .headers(headers -> headers.location(
                                URI.create(String.format("%s/%s", request.getPath(), createdTag.getId())))))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Delete tag
     *
     * @param id, id of {@link Tag} to delete
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteTag(UUID id) {
        tagServices.delete(id); // TODO MATT blocking call
        return HttpResponse.ok();
    }

    /**
     * Get tag based off id
     *
     * @param id {@link UUID} of the tag entry
     * @return {@link Tag}
     */
    @Get("/{id}")
    public Mono<HttpResponse<Tag>> readTag(@NotNull UUID id) {

        return Mono.fromCallable(() -> {
            Tag result = tagServices.read(id);
            if (result == null) {
                throw new NotFoundException("No tag for UUID");
                }
                return result;
        })
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(tag -> (HttpResponse<Tag>)HttpResponse.ok(tag))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Find tag that match all filled in parameters, return all results when given no params
     *
     * @param name {@link String} of tag
     * @return {@link Set <tag > set of tags
     */
    @Get("/{?name}")
    public Mono<HttpResponse<Set<Tag>>> findTags(@Nullable String name) {
        return Mono.fromCallable(() -> tagServices.findByFields(name))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(tag -> (HttpResponse<Set<Tag>>)HttpResponse
                        .ok(tag)).subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Update a tag
     *
     * @param tag, {@link Tag}
     * @return {@link Tag}
     */
    @Put()
    public Mono<HttpResponse<Tag>> update(@Body @Valid Tag tag, HttpRequest<?> request) {

        return Mono.fromCallable(() -> tagServices.update(tag))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(tag1 -> (HttpResponse<Tag>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), tag1.getId()))))
                        .body(tag1))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }


}
