package com.objectcomputing.checkins.services.feedback_template;

import com.objectcomputing.checkins.services.feedback.Feedback;

import java.util.List;
import java.util.UUID;

public interface FeedbackTemplateServices {

    FeedbackTemplate save(FeedbackTemplate feedbackTemplate);

    FeedbackTemplate update(FeedbackTemplate feedbackTemplate);

    Boolean delete(UUID id);

    FeedbackTemplate getById(UUID id);

    List<FeedbackTemplate> findByFields(UUID createdBy, String title);

}
