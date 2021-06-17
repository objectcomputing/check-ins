package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;

import java.util.UUID;

public interface FeedbackTemplateFixture extends RepositoryFixture{

    default FeedbackTemplate createFeedbackTemplate(UUID createdBy) {
        return new FeedbackTemplate("Fake Title", "Fake Title Description amazing feedback template", createdBy, false);
    }

    default FeedbackTemplate createPrivateFeedbackTemplate(UUID createdBy) {
        return new FeedbackTemplate( "Fake Title Private", "Fake Title Private Description amazing feedback template", createdBy, true);
    }
}
