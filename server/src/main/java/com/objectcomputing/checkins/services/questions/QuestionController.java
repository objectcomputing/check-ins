package com.objectcomputing.checkins.services.questions;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Controller("/questions")
@Secured(SecurityRule.IS_ANONYMOUS)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name="questions")
public class QuestionController {

    //    an endpoint is created for create
    //    an endpoint is created for read all
    //    an endpoint is created for update

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
    //  I know these are commented out - I added them before splitting the story and will
    // need them in the future - please ignore for now
//
//    /**
//     * Find and read a question or questions given its id or text.
//     *
//     * @param questionid
//     * @param text
//     * @return
//     */
//
//    @Get("/{?questionid,text}")
//    public List<Question> findByValue(@Nullable UUID questionid, @Nullable String text) {
//
//        List<Question> found = questionService.findByValue(questionid, text);
//        return found;
//
//    }
//
//    /**
//     * Update the pending status of a skill.
//     * @param question
//     * @return
//     */
//    @Put("/")
//    public HttpResponse<?> update(@Body @Valid Question question) {
//
//        if(null != question.getQuestionid()) {
//            Question updatedQuestion = questionService.update(question);
//            return HttpResponse
//                    .ok()
//                    .headers(headers -> headers.location(location(updatedQuestion.getQuestionid())))
//                    .body(updatedQuestion);
//        }
//
//        return HttpResponse.badRequest();
//    }

    protected URI location(UUID uuid) {
        return URI.create("/questions/" + uuid);
    }

}
