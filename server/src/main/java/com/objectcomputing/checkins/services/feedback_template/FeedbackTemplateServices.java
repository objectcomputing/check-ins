package com.objectcomputing.checkins.services.feedback_template;

import io.micronaut.core.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public interface FeedbackTemplateServices {

    FeedbackTemplate save(FeedbackTemplate feedbackTemplate);

    FeedbackTemplate update(FeedbackTemplate feedbackTemplate);

    void delete(UUID id);

    FeedbackTemplate getById(UUID id);

    List<FeedbackTemplate> findByFields(UUID creatorId, String title);

    boolean setAdHocInactiveByCreator(@Nullable UUID creatorId);

}
