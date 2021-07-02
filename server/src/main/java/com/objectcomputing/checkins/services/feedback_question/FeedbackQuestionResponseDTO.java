package com.objectcomputing.checkins.services.feedback_question;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class FeedbackQuestionResponseDTO {

    @NotNull
    @Schema(description = "unique id of the feedback question", required = true)
    private UUID id;

    @NotNull
    @Schema(description = "text of the question to receive feedback on", required = true)
    private String question;

    @NotNull
    @Schema(description = "id of the template this question is a part of", required = true)
    private UUID templateId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public UUID getTemplateId() {
        return templateId;
    }

    public void setTemplateId(UUID templateId) {
        this.templateId = templateId;
    }
}
