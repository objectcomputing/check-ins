package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestion;

public interface TemplateQuestionFixture extends RepositoryFixture {

    default TemplateQuestion createDefaultTemplateQuestion() {
        return new TemplateQuestion("How are you doing today?", 1, "TEXT");
    }

    default TemplateQuestion createSecondDefaultTemplateQuestion() {
        return new TemplateQuestion("How is the project going?", 2, "TEXT");
    }

    default TemplateQuestion saveTemplateQuestion(FeedbackTemplate template, Integer questionNumber) {
        return getTemplateQuestionRepository().save(new TemplateQuestion("How are you?", template.getId(), questionNumber, "TEXT"));
    }

    default TemplateQuestion saveAnotherTemplateQuestion(FeedbackTemplate template, Integer questionNumber) {
        return getTemplateQuestionRepository().save(new TemplateQuestion("How is the project going so far?", template.getId(), questionNumber, "TEXT"));
    }
}
