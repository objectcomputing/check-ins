package com.objectcomputing.checkins.services.feedback_answer.question_and_answer;

import java.util.UUID;

public interface QuestionAndAnswerServices {

    class Tuple<TemplateQuestion, FeedbackAnswer> {
        public TemplateQuestion question;
        public FeedbackAnswer answer;

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

        public Tuple(TemplateQuestion question, FeedbackAnswer answer) {
            this.question = question;
            this.answer = answer;
        }
        public Tuple() {}
    }

    Tuple getQuestionAndAnswer(UUID requestId, UUID questionId);

}
