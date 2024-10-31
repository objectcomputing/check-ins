package com.objectcomputing.checkins.services.questions;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller(QuestionController.PATH)
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "questions")
class QuestionController {

    public static final String PATH = "/services/questions";
    private final QuestionServices questionService;

    QuestionController(QuestionServices questionService) {
        this.questionService = questionService;
    }

    /**
     * Create and save a new question.
     *
     * @param question, {@link QuestionCreateDTO}
     * @return {@link HttpResponse<QuestionResponseDTO>}
     */

    @Post
    HttpResponse<QuestionResponseDTO> createAQuestion(@Body @Valid QuestionCreateDTO question) {
        Question newQuestion = questionService.saveQuestion(toModel(question));
        URI location = UriBuilder.of(PATH).path(newQuestion.getId().toString()).build();
        return HttpResponse.created(fromModel(newQuestion))
                .headers(headers -> headers.location(location));
    }

    /**
     * Find and read a question given its id.
     *
     * @param id {@link UUID} of the question entry
     * @return {@link QuestionResponseDTO}
     */
    @Get("/{id}")
    public QuestionResponseDTO getById(UUID id) {
        Question found = questionService.findById(id);
        if (found == null) {
            throw new NotFoundException("No question for UUID");
        }
        return fromModel(found);
    }

    /**
     * Find questions with a particular string, a particular categoryId or read all questions.
     *
     * @param text, the text of the question
     * @param categoryId, the category id of the question
     * @return {@link Set<QuestionResponseDTO>}
     */
    @Get("/{?text,categoryId}")
    Set<QuestionResponseDTO> findByText(@Nullable String text, @Nullable UUID categoryId) {
        Set<Question> questions;
        if (text != null) {
             questions = questionService.findByText(text);
        } else if (categoryId != null) {
            questions = questionService.findByCategoryId(categoryId);
        } else {
            questions = questionService.readAllQuestions();
        }
        return questions.stream()
                .map(this::fromModel)
                .collect(Collectors.toSet());
    }

    /**
     * Update the text of a question.
     *
     * @param question, {@link QuestionUpdateDTO}
     * @return {@link HttpResponse< QuestionResponseDTO >}
     */
    @Put
    HttpResponse<QuestionResponseDTO> update(@Body @Valid QuestionUpdateDTO question, HttpRequest<?> request) {
        if (question == null) {
            return HttpResponse.ok();
        }
        Question updatedQuestion = questionService.update(toModel(question));
        URI location = UriBuilder.of(PATH).path(updatedQuestion.getId().toString()).build();
        return HttpResponse.created(fromModel(updatedQuestion))
                .headers(headers -> headers.location(location));
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
