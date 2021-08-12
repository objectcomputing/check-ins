package com.objectcomputing.checkins.services.feedback_answer.question_and_answer;

import java.util.List;
import java.util.UUID;

import com.objectcomputing.checkins.services.feedback_answer.FeedbackAnswer;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestion;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import io.micronaut.core.annotation.Nullable;

public interface QuestionAndAnswerServices {

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

        public TemplateQuestion getQuestion() {
            return question;
        }

        public void setQuestion(TemplateQuestion question) {
            this.question = question;
        }

        public void setAnswer(FeedbackAnswer answer ) {
            this.answer = answer;
        }

        public FeedbackAnswer getAnswer() {
            return answer;
        }

        public FeedbackRequest getRequest() {
            return request;
        }

        public void setRequest(FeedbackRequest request) {
            this.request = request;
        }
    }

    List<Tuple> getAllQuestionsAndAnswers(UUID requestId);

    Tuple getQuestionAndAnswer(@Nullable UUID requestId, @Nullable UUID questionId);

}
