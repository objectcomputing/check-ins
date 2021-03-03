package com.objectcomputing.checkins.services.tags;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import com.objectcomputing.checkins.services.tags.EntityTag.EntityType;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

        @Controller("/services/entity_tag")
        @Secured(SecurityRule.IS_AUTHENTICATED)
        @Produces(MediaType.APPLICATION_JSON)
        @io.swagger.v3.oas.annotations.tags.Tag(name = "entity_tag")

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
         * @return {@link HttpResponse< EntityTag >}
         */

        @Post()
        public Single<HttpResponse<EntityTag>> createAEntityTag(@Body @Valid @NotNull EntityTagCreateDTO entityTag, HttpRequest<EntityTagCreateDTO> request) {

            return Single.fromCallable(() -> entityTagServices.save(new EntityTag(entityTag.getEntityId(),
                    entityTag.getTagId(), entityTag.getType())))
                    .observeOn(Schedulers.from(eventLoopGroup))
                    .map(createdEntityTag -> (HttpResponse<EntityTag>)HttpResponse
                            .created(createdEntityTag)
                            .headers(headers -> headers.location(
                                    URI.create(String.format("%s/%s", request.getPath(), createdEntityTag.getId()))))).subscribeOn(Schedulers.from(ioExecutorService));
        }

        /**
         * Delete Entity Tag
         *
         * @param id, id of {@link EntityTag} to delete
         */
        @Delete("/{id}")
        public HttpResponse<?> deleteEntityTag(@NotNull UUID id) {
            entityTagServices.delete(id);
            return HttpResponse
                    .ok();
        }

        /**
         * Get EntityTag based off id
         *
         * @param id {@link UUID} of the entity tag entry
         * @return {@link EntityTag}
         */

        @Get("/{id}")
        public Single<HttpResponse<EntityTag>> readEntityTag(@NotNull UUID id) {

            return Single.fromCallable(() -> {
                EntityTag result = entityTagServices.read(id);
                if (result == null) {
                    throw new NotFoundException("No entity tag for UUID");
                }
                return result;
            })
                    .observeOn(Schedulers.from(eventLoopGroup))
                    .map(entityTag -> (HttpResponse<EntityTag>)HttpResponse.ok(entityTag))
                    .subscribeOn(Schedulers.from(ioExecutorService));
        }

        /**
         * Find member skills that match all filled in parameters, return all results when given no params
         *
         * @param entityId {@link UUID} of entity tag
         * @param tagId  {@link UUID} of tags
         * @return {@link List < MemberSkill > list of Member Skills}
         */
        @Get("/{?entityId,tagId}")
        public Single<HttpResponse<Set<EntityTag>>> findEntityTag(@Nullable UUID entityId,
                                                                       @Nullable UUID tagId,
                                                                  @Nullable EntityType type
        ) {
            return Single.fromCallable(() -> entityTagServices.findByFields(entityId, tagId, type))
                    .observeOn(Schedulers.from(eventLoopGroup))
                    .map(entityTags -> (HttpResponse<Set<EntityTag>>)HttpResponse
                            .ok(entityTags)).subscribeOn(Schedulers.from(ioExecutorService));
        }
}
