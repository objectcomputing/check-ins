package com.objectcomputing.checkins.services.questions;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller("/services/questions")
@Secured(SecurityRule.IS_ANONYMOUS)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name="questions")
public class QuestionController {

    @Inject
    private QuestionServices questionService;

    public void setQuestionService(QuestionServices questionService) {
        this.questionService = questionService;
    }

    /**
     * Create and save a new question.
     *
     * @param question
     * @return
     */

    @Post(value = "/")
    public HttpResponse<Question> createAQuestion(@Body @Valid Question question) {
        Question newQuestion = questionService.saveQuestion(question);

        if (newQuestion == null) {
            return HttpResponse.status(HttpStatus.valueOf(409), "already exists");
        } else {
            return HttpResponse
                    .created(newQuestion)
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
    public Question getById(UUID questionid) {
        Question found = questionService.findByQuestionId(questionid);
        return found;

    }

    /**
     * Find questions with a paticular string or read all questions.
     *
     * @param text
     * * @return
     */
    @Get("/{?text}")
    public List<Question> findByText(Optional<String> text) {
        List<Question> found = null;
        if(text.isPresent()) {
            found = questionService.findByText(text.get());
        } else {
            found = questionService.readAllQuestions();
        }
        return found;

    }

    /**
     * Update the text of a question.
     * @param question
     * @return
     */
    @Put("/")
    public HttpResponse<?> update(@Body @Valid Question question) {

        if(question.getQuestionid() != null) {
            Question updatedQuestion = questionService.update(question);
            if (updatedQuestion != null) {
                return HttpResponse
                        .ok()
                        .headers(headers -> headers.location(location(updatedQuestion.getQuestionid())))
                        .body(updatedQuestion);
            } else {
                return HttpResponse.badRequest();
            }
        }

        return HttpResponse.badRequest();
    }


    protected URI location(UUID uuid) {
        return URI.create("/questions/" + uuid);
    }

}
