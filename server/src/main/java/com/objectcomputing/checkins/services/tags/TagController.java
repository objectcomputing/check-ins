package com.objectcomputing.checkins.services.tags;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.core.annotation.Nullable;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@Controller("/services/tags")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@io.swagger.v3.oas.annotations.tags.Tag(name = "tags")
public class TagController {

    private final TagServices tagServices;

    public TagController(TagServices tagServices) {
        this.tagServices = tagServices;
    }

    /**
     * Create and save a new tag.
     *
     * @param tag, {@link TagCreateDTO}
     * @return {@link HttpResponse<  Tag  >}
     */
    @Post
    public HttpResponse<Tag> createTag(@Body @Valid @NotNull TagCreateDTO tag, HttpRequest<?> request) {
        Tag createdTag = tagServices.save(new Tag(tag.getName()));
        return HttpResponse.created(createdTag)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdTag.getId()))));
    }

    /**
     * Delete tag
     *
     * @param id, id of {@link Tag} to delete
     */
    @Delete("/{id}")
    @Status(HttpStatus.OK)
    public void deleteTag(UUID id) {
        tagServices.delete(id);
    }

    /**
     * Get tag based off id
     *
     * @param id {@link UUID} of the tag entry
     * @return {@link Tag}
     */
    @Get("/{id}")
    public Tag readTag(@NotNull UUID id) {
        Tag result = tagServices.read(id);
        if (result == null) {
            throw new NotFoundException("No tag for UUID");
        }
        return result;
    }

    /**
     * Find tag that match all filled in parameters, return all results when given no params
     *
     * @param name {@link String} of tag
     * @return {@link Set <tag > set of tags
     */
    @Get("/{?name}")
    public Set<Tag> findTags(@Nullable String name) {
        return tagServices.findByFields(name);
    }

    /**
     * Update a tag
     *
     * @param tag, {@link Tag}
     * @return {@link Tag}
     */
    @Put
    public HttpResponse<Tag> update(@Body @Valid Tag tag, HttpRequest<?> request) {
        Tag tag1 = tagServices.update(tag);
        return HttpResponse.ok(tag1)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), tag1.getId()))));
    }
}
