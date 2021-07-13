package com.objectcomputing.checkins.services.feedback_template;

import com.objectcomputing.checkins.services.feedback.Feedback;
import com.objectcomputing.checkins.services.guild.GuildResponseDTO;

import java.util.List;
import java.util.UUID;

public interface FeedbackTemplateServices {

    FeedbackTemplateResponseDTO read(UUID id);

    FeedbackTemplateResponseDTO save(FeedbackTemplateCreateDTO feedbackTemplate);

    FeedbackTemplateResponseDTO update(FeedbackTemplateUpdateDTO feedbackTemplate);

    Boolean delete(UUID id);

    FeedbackTemplate getById(UUID id);

    List<FeedbackTemplate> findByFields(UUID createdBy, String title, Boolean onlyActive);

}
