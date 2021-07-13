package com.objectcomputing.checkins.services.feedback_template;

import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionCreateDTO;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionResponseDTO;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Introspected
public class FeedbackTemplateResponseDTO {

    @NotNull
    @Schema(description = "id of the feedback template", required = true)
    private UUID id;

    @NotBlank
    @Schema(description = "title of the feedback template", required = true)
    private String title;

    @Nullable
    @Schema(description = "description of the feedback template")
    private String description;

    @NotNull
    @Schema(description = "ID of person who created the feedback template", required = true)
    private UUID createdBy;

    @NotNull
    @Schema(description = "template is active", required=true)
    private Boolean active;

    @Schema(description = "Questions attached to this template")
    private List<TemplateQuestionResponseDTO> templateQuestions;

    public void setId(UUID id) {
        this.id = id;
    }
    public UUID getId() {
        return id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setActive(Boolean active){this.active = active;}

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getActive() {return active;}

    public String getDescription() {
        return description;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public List<TemplateQuestionResponseDTO> getTemplateQuestions() {
        if (templateQuestions == null) {
            templateQuestions = new ArrayList<>();
        }
        return templateQuestions;
    }

    public void setTemplateQuestions(List<TemplateQuestionResponseDTO> templateQuestions) {
        this.templateQuestions = templateQuestions;
    }

}
