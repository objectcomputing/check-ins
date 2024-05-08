package com.objectcomputing.checkins.services.question_category;

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
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

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
    private final Scheduler scheduler;

    public QuestionCategoryController(QuestionCategoryServices questionCategoryService,
                                      EventLoopGroup eventLoopGroup,
                                      @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.questionCategoryService = questionCategoryService;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = Schedulers.fromExecutorService(ioExecutorService);
    }

    /**
     * Create and save a new question category.
     *
     * @param questionCategory, {@link QuestionCategoryCreateDTO}
     * @return {@link HttpResponse<QuestionCategory>}
     */

    @Post()
    public Mono<HttpResponse<QuestionCategory>> createAQuestionCategory(@Body @Valid QuestionCategoryCreateDTO questionCategory,
                                                                        HttpRequest<?> request) {

        return Mono.fromCallable(() -> questionCategoryService.saveQuestionCategory(new QuestionCategory(questionCategory.getName())))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(createdQuestionCategory -> {
                    return (HttpResponse<QuestionCategory>) HttpResponse.created(createdQuestionCategory)
                            .headers(headers -> headers.location(
                                    URI.create(String.format("%s/%s", request.getPath(), createdQuestionCategory.getId()))));
                }).subscribeOn(scheduler);

    }

    /**
     * Find and read a question category or categories given its id, or name, if both are blank get all question categories.
     *
     * @param id,    id of the question category
     * @param name,  name of the question category
     * @return {@link Set < QuestionCategory > list of Question Categories}
     */

    @Get("/{?id,name}")
    public Mono<HttpResponse<Set<QuestionCategory>>> findByValue(@Nullable UUID id,
                                                                   @Nullable String name) {
        return Mono.fromCallable(() -> questionCategoryService.findByValue(name, id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(questionCategories -> (HttpResponse<Set<QuestionCategory>>) HttpResponse.ok(questionCategories))
                .subscribeOn(scheduler);
    }

    /**
     * Update a question Category.
     *
     * @param questionCategory, {@link QuestionCategory}
     * @return {@link HttpResponse<QuestionCategory>}
     */
    @Put()
    public Mono<HttpResponse<QuestionCategory>> update(@Body @Valid QuestionCategory questionCategory, HttpRequest<?> request) {

        return Mono.fromCallable(() -> questionCategoryService.update(questionCategory))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(updatedQuestionCategory -> (HttpResponse<QuestionCategory>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedQuestionCategory.getId()))))
                        .body(updatedQuestionCategory))
                .subscribeOn(scheduler);
    }

    /**
     * Delete A Question Category.
     *
     * @param id, id of {@link QuestionCategory} to delete
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteQuestionCategory(@NotNull UUID id) {
        questionCategoryService.delete(id); // todo matt blocking
        return HttpResponse
                .ok();
    }

}
