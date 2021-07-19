package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.frozen_template_questions.FrozenTemplateQuestion;

import java.util.UUID;

public interface FrozenTemplateQuestionFixture extends RepositoryFixture {
    default FrozenTemplateQuestion createDefaultFrozenTemplateQuestion(UUID requestId) {
        return getFeedbackRequestQuestionRepository().save(new FrozenTemplateQuestion(
                requestId,
                "How are you?",
                1));
    }

    default FrozenTemplateQuestion createAnotherDefaultFrozenTemplateQuestion(UUID requestId) {
        return getFeedbackRequestQuestionRepository().save(new FrozenTemplateQuestion(
                requestId,
                "Do you like opossums more than other animals?",
                2));
    }


}
