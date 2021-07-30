package com.objectcomputing.checkins.services.feedback_template;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public interface FeedbackTemplateServices {

    FeedbackTemplate save(FeedbackTemplate feedbackTemplate);

    FeedbackTemplate update(FeedbackTemplate feedbackTemplate);

    Boolean delete(UUID id);

    FeedbackTemplate getById(UUID id);

    List<FeedbackTemplate> findByFields(UUID creatorId, String title);

    Boolean setAdHocInactiveByCreator(@Nullable UUID creatorId);

}
