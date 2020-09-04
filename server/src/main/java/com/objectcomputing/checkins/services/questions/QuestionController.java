package com.objectcomputing.checkins.services.questions;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller("/services/questions")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name="questions")
public class QuestionController {

    @Inject
    private QuestionServices questionService;

    @Error(exception = QuestionNotFoundException.class)
    public HttpResponse<?> handleNotFound(HttpRequest<?> request, QuestionNotFoundException e) {
        JsonError error = new JsonError(e.getMessage()).link(Link.SELF, Link.of(request.getUri()));
        return HttpResponse.notFound().body(error);
    }

    @Error(exception = QuestionDuplicateException.class)
    public HttpResponse<?> handleDupe(HttpRequest<?> request, QuestionDuplicateException e) {
        JsonError error = new JsonError(e.getMessage()).link(Link.SELF, Link.of(request.getUri()));
        return HttpResponse.status(HttpStatus.CONFLICT).body(error);
    }

    @Error(exception = QuestionBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, QuestionBadArgException e) {
        JsonError error = new JsonError(e.getMessage()).link(Link.SELF, Link.of(request.getUri()));
        return HttpResponse.badRequest().body(error);
    }

    /**
     * Create and save a new question.
     *
     * @param question, {@link QuestionCreateDTO}
     * @return {@link HttpResponse<QuestionResponseDTO>}
     */

    @Post(value = "/")
    public HttpResponse<QuestionResponseDTO> createAQuestion(@Body @Valid QuestionCreateDTO question, HttpRequest<QuestionCreateDTO> request) {
        Question newQuestion = questionService.saveQuestion(toModel(question));

        return HttpResponse
                .created(fromModel(newQuestion))
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), newQuestion.getId()))));

    }

    /**
     * Find and read a question given its id.
     *
     * @param id {@link UUID} of the question entry
     * @return {@link HttpResponse< QuestionResponseDTO >}
     */

    @Get("/{id}")
    public HttpResponse<QuestionResponseDTO> getById(UUID id) {
        Question found = questionService.findById(id);

        return HttpResponse.ok(fromModel(found));
    }

    /**
     * Find questions with a particular string or read all questions.
     *
     * @param text, the text of the question
     * * @return {@link List HttpResponse< QuestionResponseDTO >}
     */
    @Get("/{?text}")
    public HttpResponse<List<QuestionResponseDTO>> findByText(@Nullable Optional<String> text) {
        Set<Question> found = null;
        if(text.isPresent()) {
            found = questionService.findByText(text.get());
        } else {
            found = questionService.readAllQuestions();
        }

        List<QuestionResponseDTO> responseBody = found.stream()
                .map(question -> fromModel(question))
                .collect(Collectors.toList());
        return HttpResponse.ok(responseBody);

    }

    /**
     * Update the text of a question.
     * @param question, {@link QuestionUpdateDTO}
     * @return {@link HttpResponse< QuestionResponseDTO >}
     */
    @Put("/")
    public HttpResponse<QuestionResponseDTO> update(@Body @Valid QuestionUpdateDTO question, HttpRequest<QuestionCreateDTO> request) {

        Question updatedQuestion = questionService.update(toModel(question));

        return HttpResponse
                .created(fromModel(updatedQuestion))
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), updatedQuestion.getId()))));

    }

    private QuestionResponseDTO fromModel(Question question) {
        QuestionResponseDTO qrdto =  new QuestionResponseDTO();
        qrdto.setQuestionId(question.getId());
        qrdto.setText(question.getText());
        return qrdto;
    }

    private Question toModel(QuestionUpdateDTO dto) {
        Question model =  new Question();
        model.setId(dto.getId());
        model.setText(dto.getText());
        return model;
    }

    private Question toModel(QuestionCreateDTO dto) {
        Question model =  new Question();
        model.setText(dto.getText());
        return model;
    }

}
