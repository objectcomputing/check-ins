package com.objectcomputing.checkins.services.question_category;

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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@Controller("/services/question-categories")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
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
    public HttpResponse<QuestionCategory> createAQuestionCategory(@Body @Valid QuestionCategoryCreateDTO questionCategory,
                                                                  HttpRequest<?> request) {

        QuestionCategory createdQuestionCategory = questionCategoryService.saveQuestionCategory(new QuestionCategory(questionCategory.getName()));
        return HttpResponse.created(createdQuestionCategory)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdQuestionCategory.getId()))));
    }

    /**
     * Find and read a question category or categories given its id, or name, if both are blank get all question categories.
     *
     * @param id,   id of the question category
     * @param name, name of the question category
     * @return {@link Set < QuestionCategory > list of Question Categories}
     */

    @Get("/{?id,name}")
    public Set<QuestionCategory> findByValue(@Nullable UUID id, @Nullable String name) {
        return questionCategoryService.findByValue(name, id);
    }

    /**
     * Update a question Category.
     *
     * @param questionCategory, {@link QuestionCategory}
     * @return {@link HttpResponse<QuestionCategory>}
     */
    @Put
    public HttpResponse<QuestionCategory> update(@Body @Valid QuestionCategory questionCategory, HttpRequest<?> request) {
        QuestionCategory updatedQuestionCategory = questionCategoryService.update(questionCategory);
        return HttpResponse.ok(updatedQuestionCategory)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedQuestionCategory.getId()))));
    }

    /**
     * Delete A Question Category.
     *
     * @param id, id of {@link QuestionCategory} to delete
     */
    @Delete("/{id}")
    @Status(HttpStatus.OK)
    public void deleteQuestionCategory(@NotNull UUID id) {
        questionCategoryService.delete(id);
    }
}
