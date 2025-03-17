package com.objectcomputing.checkins.services.feedback_answer.question_and_answer;

import com.objectcomputing.checkins.services.feedback_answer.FeedbackAnswer;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestion;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

public interface QuestionAndAnswerServices {

    @Setter
    @Getter
    @Introspected
    class Tuple {
        private TemplateQuestion question;
        private FeedbackAnswer answer;
        private FeedbackRequest request;

        public Tuple(TemplateQuestion question, FeedbackAnswer answer) {
            this.question = question;
            this.answer = answer;
        }

        public Tuple(TemplateQuestion question, FeedbackAnswer answer, FeedbackRequest request) {
            this.question = question;
            this.answer = answer;
            this.request = request;
        }

        public Tuple() {}

    }

    List<Tuple> getAllQuestionsAndAnswers(UUID requestId);

    Tuple getQuestionAndAnswer(@Nullable UUID requestId, @Nullable UUID questionId);

}
