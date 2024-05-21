package com.objectcomputing.checkins.services.questions;

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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller("/services/questions")
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "questions")
public class QuestionController {

    private final QuestionServices questionService;

    public QuestionController(QuestionServices questionService) {
        this.questionService = questionService;
    }

    /**
     * Create and save a new question.
     *
     * @param question, {@link QuestionCreateDTO}
     * @return {@link HttpResponse<QuestionResponseDTO>}
     */

    @Post
    public Mono<HttpResponse<QuestionResponseDTO>> createAQuestion(@Body @Valid QuestionCreateDTO question, HttpRequest<?> request) {
        return Mono.fromCallable(() -> questionService.saveQuestion(toModel(question)))
                .map(newQuestion -> HttpResponse.created(fromModel(newQuestion))
                        .headers(headers -> headers.location(
                                URI.create(String.format("%s/%s", request.getPath(), newQuestion.getId())))));

    }

    /**
     * Find and read a question given its id.
     *
     * @param id {@link UUID} of the question entry
     * @return {@link HttpResponse< QuestionResponseDTO >}
     */
    @Get("/{id}")
    public Mono<HttpResponse<QuestionResponseDTO>> getById(UUID id) {
        return Mono.fromCallable(() -> {
            Question found = questionService.findById(id);
            if (found == null) {
                throw new NotFoundException("No question for UUID");
            }
            return found;
        }).map(question -> HttpResponse.ok(fromModel(question)));
    }

    /**
     * Find questions with a particular string, a particular categoryId or read all questions.
     *
     * @param text, the text of the question
     * @param categoryId, the category id of the question
     * @return {@link List HttpResponse<QuestionResponseDTO >
     */
    @Get("/{?text,categoryId}")
    public Mono<HttpResponse<Set<QuestionResponseDTO>>> findByText(@Nullable String text, @Nullable UUID categoryId) {
        return Mono.fromCallable(() -> {
            if (text != null) {
                return questionService.findByText(text);
            } else if (categoryId != null) {
                return questionService.findByCategoryId(categoryId);
            } else {
                return questionService.readAllQuestions();
            }
        })
        .map(questions -> {
            Set<QuestionResponseDTO> responseBody = questions.stream()
                    .map(this::fromModel)
                    .collect(Collectors.toSet());
            return HttpResponse.ok(responseBody);
        });
    }

    /**
     * Update the text of a question.
     *
     * @param question, {@link QuestionUpdateDTO}
     * @return {@link HttpResponse< QuestionResponseDTO >}
     */
    @Put
    public Mono<HttpResponse<QuestionResponseDTO>> update(@Body @Valid QuestionUpdateDTO question, HttpRequest<?> request) {
        if (question == null) {
            return Mono.just(HttpResponse.ok());
        }
        return Mono.fromCallable(() -> questionService.update(toModel(question)))
                .map(updatedQuestion -> HttpResponse.created(fromModel(updatedQuestion))
                        .headers(headers -> headers.location(
                                URI.create(String.format("%s/%s", request.getPath(), updatedQuestion.getId())))));

    }

    private QuestionResponseDTO fromModel(Question question) {
        QuestionResponseDTO qrdto = new QuestionResponseDTO();
        qrdto.setId(question.getId());
        qrdto.setText(question.getText());
        if (question.getCategoryId() != null) {
            qrdto.setCategoryId(question.getCategoryId());
        }
        return qrdto;
    }

    private Question toModel(QuestionUpdateDTO dto) {
        Question model = new Question();
        model.setId(dto.getId());
        model.setText(dto.getText());
        if (dto.getCategoryId() != null) {
            model.setCategoryId(dto.getCategoryId());
        }
        return model;
    }

    private Question toModel(QuestionCreateDTO dto) {
        Question model = new Question();
        model.setText(dto.getText());
        if (dto.getCategoryId() != null) {
            model.setCategoryId(dto.getCategoryId());
        }
        return model;
    }

}
