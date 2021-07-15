package com.objectcomputing.checkins.services.feedback_template;

import java.util.List;
import java.util.UUID;

public interface FeedbackTemplateServices {

    FeedbackTemplate save(FeedbackTemplateCreateDTO feedbackTemplate);

    FeedbackTemplate update(FeedbackTemplateUpdateDTO feedbackTemplate);

    Boolean delete(UUID id);

    FeedbackTemplate getById(UUID id);

    List<FeedbackTemplateResponseDTO> findByFields(UUID createdBy, String title);

}
