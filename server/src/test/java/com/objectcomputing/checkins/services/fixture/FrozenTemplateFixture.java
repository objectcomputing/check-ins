package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.frozen_template.FrozenTemplate;

import javax.annotation.Nullable;
import java.util.UUID;

public interface FrozenTemplateFixture extends RepositoryFixture{

    default FrozenTemplate saveDefaultFrozenTemplate(UUID createdBy) {
        FrozenTemplate frozenTemplate = new FrozenTemplate(
                "Default Template Title",
                "Fake template to freeze for testing",
                createdBy);
        getFrozenTemplateRepository().save(frozenTemplate);
        return frozenTemplate;

    }

    default FrozenTemplate saveAnotherFrozenTemplate(UUID createdBy) {
        FrozenTemplate frozenTemplate = new FrozenTemplate(
                "Another default template title",
                "A second fake template to freeze for testing",
                createdBy);
        getFrozenTemplateRepository().save(frozenTemplate);
        return frozenTemplate;

    }


}
