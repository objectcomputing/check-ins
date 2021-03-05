package com.objectcomputing.checkins.services.tags;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.skills.Skill;
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

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;


@Controller("/services/tag")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@io.swagger.v3.oas.annotations.tags.Tag(name = "tag")
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
        public Single<HttpResponse<Tag>> createTag(@Body @Valid @NotNull TagCreateDTO tag, HttpRequest<TagCreateDTO> request) {

            return Single.fromCallable(() -> tagServices.save(new Tag(tag.getName())))
                    .observeOn(Schedulers.from(eventLoopGroup))
                    .map(createdTag -> (HttpResponse<Tag>)HttpResponse
                            .created(createdTag)
                            .headers(headers -> headers.location(
                                    URI.create(String.format("%s/%s", request.getPath(), createdTag.getId()))))).subscribeOn(Schedulers.from(ioExecutorService));
        }

    /**
     * Delete tag
     *
     * @param id, id of {@link Tag} to delete
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteTag(UUID id) {
        tagServices.delete(id);
        return HttpResponse.ok();
    }

        /**
         * Get tag based off id
         *
         * @param id {@link UUID} of the tag entry
         * @return {@link Tag}
         */
        @Get("/{id}")
        public Single<HttpResponse<Tag>> readTag(@NotNull UUID id) {

            return Single.fromCallable(() -> {
                Tag result = tagServices.read(id);
                if (result == null) {
                    throw new NotFoundException("No tag for UUID");
                }
                return result;
            })
                    .observeOn(Schedulers.from(eventLoopGroup))
                    .map(tag -> (HttpResponse<Tag>)HttpResponse.ok(tag))
                    .subscribeOn(Schedulers.from(ioExecutorService));
        }

        /**
         * Find tag that match all filled in parameters, return all results when given no params
         *
         * @param name {@link String} of tag
         * @return {@link Set < tag > set of tags}
         */
        @Get("/{?name}")
        public Single<HttpResponse<Set<Tag>>> findtags(@Nullable String name) {
            return Single.fromCallable(() -> tagServices.findByFields(name))
                    .observeOn(Schedulers.from(eventLoopGroup))
                    .map(tag -> (HttpResponse<Set<Tag>>)HttpResponse
                            .ok(tag)).subscribeOn(Schedulers.from(ioExecutorService));
        }

    /**
     * Update a tag
     *
     * @param tag, {@link Tag}
     * @return {@link Tag}
     */
    @Put()
    public Single<HttpResponse<Tag>> update(@Body @Valid Tag tag, HttpRequest<Skill> request) {

        return Single.fromCallable(() -> tagServices.update(tag))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(tag1 -> (HttpResponse<Tag>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), tag1.getId()))))
                        .body(tag1))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }


}
