package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestion;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionCreateDTO;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionUpdateDTO;

public interface TemplateQuestionFixture extends RepositoryFixture {

    default TemplateQuestion createDefaultTemplateQuestion() {
        return new TemplateQuestion("How are you doing today?", 1);
    }

    default TemplateQuestion createSecondDefaultTemplateQuestion() {
        return new TemplateQuestion("How is the project going?", 2);
    }

    default TemplateQuestion saveTemplateQuestion(FeedbackTemplate template, Integer questionNumber) {
        return getTemplateQuestionRepository().save(new TemplateQuestion("How are you?", template.getId(), questionNumber));
    }

    default TemplateQuestion saveAnotherTemplateQuestion(FeedbackTemplate template, Integer questionNumber) {
        return getTemplateQuestionRepository().save(new TemplateQuestion("How is the project going so far?", template.getId(), questionNumber));
    }
}
