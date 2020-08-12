package com.objectcomputing.checkins.services.questions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.objectcomputing.checkins.services.role.RoleBadArgException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller("/services/questions")
@Secured(SecurityRule.IS_ANONYMOUS)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name="questions")
public class QuestionController {

    @Inject
    private QuestionServices questionService;

    public QuestionController(QuestionServices questionServices) {
        this.questionService = questionServices;
    }

    @Error(exception = QuestionNotFoundException.class)
    public HttpResponse<?> handleDupe(HttpRequest<?> request, QuestionNotFoundException e) {
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

        if (newQuestion == null) {
            throw new QuestionDuplicateException("Already exists");
            //return HttpResponse.status(HttpStatus.valueOf(409), "already exists");
        } else {
            return HttpResponse
                    .created(fromModel(newQuestion))
                    .headers(headers -> headers.location(location(newQuestion.getQuestionid())));
        }
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
        if (found == null) {
            throw new QuestionNotFoundException("No question for uuid");
        }
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
        List<Question> found = null;
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
    public HttpResponse<QuestionResponseDTO> update(@Body @Valid QuestionCreateDTO question) {

        if(question.getQuestionId() != null) {
            Question updatedQuestion = questionService.update(toModel(question));
            if (updatedQuestion != null) {
                return HttpResponse
                        .ok()
                        .headers(headers -> headers.location(location(updatedQuestion.getQuestionid())))
                        .body(fromModel(updatedQuestion));
            } else {
                throw new QuestionBadArgException("No question found for this uuid");
            }
        }

        throw new QuestionBadArgException("Question id is required");
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

    private Question toModel(QuestionCreateDTO dto) {
        Question model =  new Question();
        model.setQuestionid(dto.getQuestionId());
        model.setText(dto.getText());
        return model;
    }

}
