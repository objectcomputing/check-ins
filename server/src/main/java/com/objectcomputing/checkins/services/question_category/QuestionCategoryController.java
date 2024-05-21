package com.objectcomputing.checkins.services.question_category;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@Controller("/services/question-categories")
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "question-categories")
public class QuestionCategoryController {

    private final QuestionCategoryServices questionCategoryService;

    public QuestionCategoryController(QuestionCategoryServices questionCategoryService) {
        this.questionCategoryService = questionCategoryService;
    }

    /**
     * Create and save a new question category.
     *
     * @param questionCategory, {@link QuestionCategoryCreateDTO}
     * @return {@link HttpResponse<QuestionCategory>}
     */

    @Post
    public Mono<HttpResponse<QuestionCategory>> createAQuestionCategory(@Body @Valid QuestionCategoryCreateDTO questionCategory,
                                                                        HttpRequest<?> request) {

        return Mono.fromCallable(() -> questionCategoryService.saveQuestionCategory(new QuestionCategory(questionCategory.getName())))
                .map(createdQuestionCategory -> HttpResponse.created(createdQuestionCategory)
                        .headers(headers -> headers.location(
                                URI.create(String.format("%s/%s", request.getPath(), createdQuestionCategory.getId())))));

    }

    /**
     * Find and read a question category or categories given its id, or name, if both are blank get all question categories.
     *
     * @param id,    id of the question category
     * @param name,  name of the question category
     * @return {@link Set < QuestionCategory > list of Question Categories}
     */

    @Get("/{?id,name}")
    public Mono<HttpResponse<Set<QuestionCategory>>> findByValue(@Nullable UUID id, @Nullable String name) {
        return Mono.fromCallable(() -> questionCategoryService.findByValue(name, id))
                .map(HttpResponse::ok);
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
                .map(updatedQuestionCategory -> HttpResponse.ok(updatedQuestionCategory)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedQuestionCategory.getId())))));
    }

    /**
     * Delete A Question Category.
     *
     * @param id, id of {@link QuestionCategory} to delete
     */
    @Delete("/{id}")
    public Mono<HttpResponse<?>> deleteQuestionCategory(@NotNull UUID id) {
        return Mono.fromCallable(() -> questionCategoryService.delete(id))
                .thenReturn(HttpResponse.ok());
    }

}
