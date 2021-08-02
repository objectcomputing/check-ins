package com.objectcomputing.checkins.services.question_category;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.core.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/question-categories")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "question-categories")
public class QuestionCategoryController {

    private final QuestionCategoryServices questionCategoryService;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public QuestionCategoryController(QuestionCategoryServices questionCategoryService,
                                      EventLoopGroup eventLoopGroup,
                                      ExecutorService ioExecutorService) {
        this.questionCategoryService = questionCategoryService;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Create and save a new question category.
     *
     * @param questionCategory, {@link QuestionCategoryCreateDTO}
     * @return {@link HttpResponse<QuestionCategory>}
     */

    @Post()
    public Single<HttpResponse<QuestionCategory>> createAQuestionCategory(@Body @Valid QuestionCategoryCreateDTO questionCategory,
                                                                          HttpRequest<QuestionCategoryCreateDTO> request) {

        return Single.fromCallable(() -> questionCategoryService.saveQuestionCategory(new QuestionCategory(questionCategory.getName())))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(createdQuestionCategory -> {
                    return (HttpResponse<QuestionCategory>) HttpResponse.created(createdQuestionCategory)
                            .headers(headers -> headers.location(
                                    URI.create(String.format("%s/%s", request.getPath(), createdQuestionCategory.getId()))));
                }).subscribeOn(Schedulers.from(ioExecutorService));

    }

    /**
     * Find and read a question category or categories given its id, or name, if both are blank get all question categories.
     *
     * @param id,    id of the question category
     * @param name,  name of the question category
     * @return {@link Set < QuestionCategory > list of Question Categories}
     */

    @Get("/{?id,name}")
    public Single<HttpResponse<Set<QuestionCategory>>> findByValue(@Nullable UUID id,
                                                                   @Nullable String name) {
        return Single.fromCallable(() -> questionCategoryService.findByValue(name, id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(questionCategories -> (HttpResponse<Set<QuestionCategory>>) HttpResponse.ok(questionCategories))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Update a question Category.
     *
     * @param questionCategory, {@link QuestionCategory}
     * @return {@link HttpResponse<QuestionCategory>}
     */
    @Put()
    public Single<HttpResponse<QuestionCategory>> update(@Body @Valid QuestionCategory questionCategory, HttpRequest<QuestionCategory> request) {

        return Single.fromCallable(() -> questionCategoryService.update(questionCategory))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(updatedQuestionCategory -> (HttpResponse<QuestionCategory>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedQuestionCategory.getId()))))
                        .body(updatedQuestionCategory))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Delete A Question Category.
     *
     * @param id, id of {@link QuestionCategory} to delete
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteQuestionCategory(@NotNull UUID id) {
        questionCategoryService.delete(id);
        return HttpResponse
                .ok();
    }

}
