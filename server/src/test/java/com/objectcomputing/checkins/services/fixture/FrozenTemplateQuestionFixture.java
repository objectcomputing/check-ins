package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.frozen_template_questions.FrozenTemplateQuestion;

import java.util.UUID;

public interface FrozenTemplateQuestionFixture extends RepositoryFixture {
    default FrozenTemplateQuestion createDefaultFrozenTemplateQuestion(UUID frozenTemplateId) {
        return getFeedbackRequestQuestionRepository().save(new FrozenTemplateQuestion(
                frozenTemplateId,
                "How are you?",
                1));
    }

    default FrozenTemplateQuestion createAnotherDefaultFrozenTemplateQuestion(UUID frozenTemplateId) {
        return getFeedbackRequestQuestionRepository().save(new FrozenTemplateQuestion(
                frozenTemplateId,
                "Do you like opossums more than other animals?",
                2));
    }


}