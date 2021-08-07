package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;

import java.util.UUID;

public interface FeedbackTemplateFixture extends RepositoryFixture{

    default FeedbackTemplate createFeedbackTemplate(UUID creatorId) {
        return new FeedbackTemplate("Fake Title", "Fake Title Description amazing feedback template", creatorId, true, true);
    }

    default FeedbackTemplate createAnotherFeedbackTemplate(UUID creatorId) {
        return new FeedbackTemplate( "Fake Title 2", "Fake Title Private Description amazing feedback template 2", creatorId, true, false);
    }

    default FeedbackTemplate createAThirdFeedbackTemplate(UUID creatorId) {
        return new FeedbackTemplate( "Something completely different", "Fake Title Private Description amazing feedback template 3", creatorId, true, true);
    }

    default FeedbackTemplate saveFeedbackTemplate(UUID creatorId) {
        return getFeedbackTemplateRepository().save(new FeedbackTemplate("Sample Template", "A saved feedback template", creatorId, true, false));
    }
}
