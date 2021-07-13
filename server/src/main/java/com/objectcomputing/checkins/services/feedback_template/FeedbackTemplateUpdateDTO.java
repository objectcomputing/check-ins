package com.objectcomputing.checkins.services.feedback_template;

import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionCreateDTO;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionUpdateDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;
import java.util.UUID;
import io.micronaut.core.annotation.Introspected;

@Introspected
public class FeedbackTemplateUpdateDTO {
    @NotNull
    @Schema(description = "id of the feedback template", required = true)
    private UUID id;

    @NotBlank
    @Schema(description = "the updated title of the feedback template", required = true)
    private String title;

    @Nullable
    @Schema(description = "the updated description of the feedback template")
    private String description;

    @NotNull
    @Schema(description = "whether the template can still be used", required = true)
    private Boolean active;

    @NotNull
    @Schema(description = "ID of person who created the feedback template", required = true)
    private UUID createdBy;

    @Schema(description = "Questions attached to this template")
    private List<TemplateQuestionUpdateDTO> templateQuestions;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(UUID id) {this.id = id;}

    public UUID getId(){ return id; }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    public UUID getCreatedBy() {return createdBy;}

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public List<TemplateQuestionUpdateDTO> getTemplateQuestions() {
        return templateQuestions;
    }

    public void setTemplateQuestions(List<TemplateQuestionUpdateDTO> templateQuestions) {
        this.templateQuestions = templateQuestions;
    }
}
