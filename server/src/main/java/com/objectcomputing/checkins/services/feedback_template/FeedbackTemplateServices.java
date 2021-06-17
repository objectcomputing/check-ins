package com.objectcomputing.checkins.services.feedback_template;

import com.objectcomputing.checkins.services.feedback.Feedback;

import java.util.List;
import java.util.UUID;

public interface FeedbackTemplateServices {

    FeedbackTemplate save(FeedbackTemplate feedbackTemplate);

    Feedback update(FeedbackTemplate feedbackTemplate);

    Boolean delete(UUID id);

    Feedback getById(UUID id);

    List<FeedbackTemplate> getByValues(String title, String description);


}
