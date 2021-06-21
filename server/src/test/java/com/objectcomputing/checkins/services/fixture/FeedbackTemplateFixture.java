package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;

import java.util.UUID;

public interface FeedbackTemplateFixture extends RepositoryFixture{

    default FeedbackTemplate createFeedbackTemplate(UUID createdBy) {
        return new FeedbackTemplate("Fake Title", "Fake Title Description amazing feedback template", createdBy);
    }

    default FeedbackTemplate createAnotherFeedbackTemplate(UUID createdBy) {
        return new FeedbackTemplate( "Fake Title 2", "Fake Title Private Description amazing feedback template 2", createdBy);
    }

    default FeedbackTemplate createAThirdFeedbackTemplate(UUID createdBy) {
        return new FeedbackTemplate( "Something completely different", "Fake Title Private Description amazing feedback template 3", createdBy);
    }
}
