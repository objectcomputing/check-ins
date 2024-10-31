package com.objectcomputing.checkins.services.feedback_answer.question_and_answer;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.util.List;
import java.util.UUID;

@Controller("/services/feedback/questions-and-answers")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
public class QuestionAndAnswerController {

    private final QuestionAndAnswerServices questionAndAnswerServices;

    public QuestionAndAnswerController(QuestionAndAnswerServices questionAndAnswerServices) {
        this.questionAndAnswerServices = questionAndAnswerServices;
    }

    @Get("/{?requestId,questionId}")
    public QuestionAndAnswerServices.Tuple getQuestionAndAnswer(@Nullable UUID requestId, @Nullable UUID questionId) {
        return questionAndAnswerServices.getQuestionAndAnswer(requestId, questionId);
    }

    @Get("/{requestId}")
    public List<QuestionAndAnswerServices.Tuple> getAllQuestionsAndAnswers(@Nullable UUID requestId) {
        return questionAndAnswerServices.getAllQuestionsAndAnswers(requestId);
    }
}
