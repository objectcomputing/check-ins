package com.objectcomputing.checkins.services.tags.entityTag;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.tags.entityTag.EntityTag.EntityType;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@Controller("/services/entity-tags")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "entity-tags")

public class EntityTagController {

    private final EntityTagServices entityTagServices;

    public EntityTagController(EntityTagServices entityTagServices) {
        this.entityTagServices = entityTagServices;
    }

    /**
     * Create and save a new entity tag.
     *
     * @param entityTag, {@link EntityTagCreateDTO}
     * @return {@link HttpResponse<  EntityTag  >}
     */
    @Post
    public HttpResponse<EntityTag> createAEntityTag(@Body @Valid @NotNull EntityTagCreateDTO entityTag, HttpRequest<?> request) {
        EntityTag createdEntityTag = entityTagServices.save(new EntityTag(entityTag.getEntityId(), entityTag.getTagId(), entityTag.getType()));
        return HttpResponse.created(createdEntityTag)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdEntityTag.getId()))));
    }

    /**
     * Delete Entity Tag
     *
     * @param id, id of {@link EntityTag} to delete
     */
    @Delete("/{id}")
    @Status(HttpStatus.OK)
    public void deleteEntityTag(@NotNull UUID id) {
        entityTagServices.delete(id);
    }

    /**
     * Get EntityTag based off id
     *
     * @param id {@link UUID} of the entity tag entry
     * @return {@link EntityTag}
     */
    @Get("/{id}")
    public EntityTag readEntityTag(@NotNull UUID id) {
        EntityTag result = entityTagServices.read(id);
        if (result == null) {
            throw new NotFoundException("No entity tag for UUID");
        }
        return result;
    }

    /**
     * Find Entity Tags that match all filled in parameters, return all results when given no params
     *
     * @param entityId {@link UUID} of entity tag
     * @param tagId    {@link UUID} of tags
     * @return set of Entity Tags
     */
    @Get("/{?entityId,tagId}")
    public Set<EntityTag> findEntityTag(@Nullable UUID entityId, @Nullable UUID tagId, @Nullable EntityType type) {
        return entityTagServices.findByFields(entityId, tagId, type);
    }
}
