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
     * @param question
     * @return
     */

    @Post(value = "/")
    public HttpResponse<QuestionResponseDTO> createAQuestion(@Body @Valid QuestionCreateDTO question) {
        Question newQuestion = questionService.saveQuestion(toModel(question));

        return HttpResponse
                .created(fromModel(newQuestion))
                .headers(headers -> headers.location(location(newQuestion.getQuestionid())));
    }

    /**
     * Find and read a question given its id.
     *
     * @param questionid
     * @return
     */

    @Get("/{questionid}")
    public HttpResponse<QuestionResponseDTO> getById(UUID questionid) {
        Question found = questionService.findByQuestionId(questionid);

        return HttpResponse.ok(fromModel(found));
    }

    /**
     * Find questions with a particular string or read all questions.
     *
     * @param text
     * * @return
     */
    @Get("/{?text}")
    public HttpResponse<List<QuestionResponseDTO>> findByText(Optional<String> text) {
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
     * @param question
     * @return
     */
    @Put("/")
    public HttpResponse<QuestionResponseDTO> update(@Body @Valid QuestionUpdateDTO question) {

        Question updatedQuestion = questionService.update(toModel(question));

        return HttpResponse
                .ok()
                .headers(headers -> headers.location(location(updatedQuestion.getQuestionid())))
                .body(fromModel(updatedQuestion));
    }


    protected URI location(UUID uuid) {
        return URI.create("/services/questions/" + uuid);
    }

    private QuestionResponseDTO fromModel(Question question) {
        QuestionResponseDTO qrdto =  new QuestionResponseDTO();
        qrdto.setQuestionId(question.getQuestionid());
        qrdto.setText(question.getText());
        return qrdto;
    }

    private Question toModel(QuestionUpdateDTO dto) {
        Question model =  new Question();
        model.setQuestionid(dto.getQuestionId());
        model.setText(dto.getText());
        return model;
    }

    private Question toModel(QuestionCreateDTO dto) {
        Question model =  new Question();
        model.setText(dto.getText());
        return model;
    }

}
