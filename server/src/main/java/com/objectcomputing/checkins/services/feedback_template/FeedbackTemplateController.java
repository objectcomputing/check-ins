package com.objectcomputing.checkins.services.feedback_template;

import com.objectcomputing.checkins.services.feedback.Feedback;
import com.objectcomputing.checkins.services.feedback.FeedbackResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class FeedbackTemplateController {



    private FeedbackResponseDTO fromEntity(Feedback feedback) {
        FeedbackResponseDTO dto = new FeedbackResponseDTO();
        dto.setId(feedback.getId());
        dto.setContent(feedback.getContent());
        dto.setSentTo(feedback.getSentTo());
        dto.setSentBy(feedback.getSentBy());
        dto.setConfidential(feedback.getConfidential());
        dto.setCreatedOn(feedback.getCreatedOn());
        dto.setUpdatedOn(feedback.getUpdatedOn());
        return dto;
    }
}
