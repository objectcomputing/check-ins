package com.objectcomputing.checkins.services.tags.entityTag;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.tags.entityTag.EntityTag.EntityType;
import io.micronaut.core.annotation.Nullable;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/entity-tags")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "entity-tags")

public class EntityTagController {

    private final EntityTagServices entityTagServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public EntityTagController(EntityTagServices entityTagServices,
                               EventLoopGroup eventLoopGroup,
                               @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.entityTagServices = entityTagServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Create and save a new entity tag.
     *
     * @param entityTag, {@link EntityTagCreateDTO}
     * @return {@link HttpResponse<  EntityTag  >}
     */
    @Post()
    public Mono<HttpResponse<EntityTag>> createAEntityTag(@Body @Valid @NotNull EntityTagCreateDTO entityTag, HttpRequest<?> request) {

        return Mono.fromCallable(() -> entityTagServices.save(new EntityTag(entityTag.getEntityId(),
                entityTag.getTagId(), entityTag.getType())))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(createdEntityTag -> (HttpResponse<EntityTag>)HttpResponse
                        .created(createdEntityTag)
                        .headers(headers -> headers.location(
                                URI.create(String.format("%s/%s", request.getPath(), createdEntityTag.getId())))))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Delete Entity Tag
     *
     * @param id, id of {@link EntityTag} to delete
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteEntityTag(@NotNull UUID id) {
        entityTagServices.delete(id); // todo matt blocking call
        return HttpResponse.ok();
    }

    /**
     * Get EntityTag based off id
     *
     * @param id {@link UUID} of the entity tag entry
     * @return {@link EntityTag}
     */
    @Get("/{id}")
    public Mono<HttpResponse<EntityTag>> readEntityTag(@NotNull UUID id) {

        return Mono.fromCallable(() -> {
            EntityTag result = entityTagServices.read(id);
            if (result == null) {
                throw new NotFoundException("No entity tag for UUID");
            }
            return result;
        })
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(entityTag -> (HttpResponse<EntityTag>)HttpResponse.ok(entityTag))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Find Entity Tags that match all filled in parameters, return all results when given no params
     *
     * @param entityId {@link UUID} of entity tag
     * @param tagId  {@link UUID} of tags
     * @return {@link List <EntityTag > list of Entity Tags
     */
    @Get("/{?entityId,tagId}")
    public Mono<HttpResponse<Set<EntityTag>>> findEntityTag(@Nullable UUID entityId,
                                                              @Nullable UUID tagId,
                                                              @Nullable EntityType type) {
        return Mono.fromCallable(() -> entityTagServices.findByFields(entityId, tagId, type))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(entityTags -> (HttpResponse<Set<EntityTag>>)HttpResponse
                        .ok(entityTags)).subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }
}
