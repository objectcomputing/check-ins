package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestion;

import java.util.UUID;

public interface TemplateQuestionFixture extends RepositoryFixture {

    default TemplateQuestion createDefaultFeedbackQuestion(UUID templateId) {
        return new TemplateQuestion("How are you doing today?",templateId, 1);
    }

}
