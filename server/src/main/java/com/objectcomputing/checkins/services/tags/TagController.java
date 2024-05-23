package com.objectcomputing.checkins.services.tags;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;

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
    @Post()
    public Mono<HttpResponse<Tag>> createTag(@Body @Valid @NotNull TagCreateDTO tag, HttpRequest<?> request) {
        return Mono.fromCallable(() -> tagServices.save(new Tag(tag.getName())))
                .map(createdTag -> HttpResponse.created(createdTag)
                        .headers(headers -> headers.location(
                                URI.create(String.format("%s/%s", request.getPath(), createdTag.getId())))));
    }

    /**
     * Delete tag
     *
     * @param id, id of {@link Tag} to delete
     */
    @Delete("/{id}")
    public Mono<HttpResponse<?>> deleteTag(UUID id) {
        return Mono.fromRunnable(() -> tagServices.delete(id))
                .thenReturn(HttpResponse.ok());
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
        }).map(HttpResponse::ok);
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
                .map(HttpResponse::ok);
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
                .map(tag1 -> HttpResponse.ok(tag1)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), tag1.getId())))));
    }

}
