package com.objectcomputing.checkins.services.feedback_template;

import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionCreateDTO;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Introspected
public class FeedbackTemplateCreateDTO {

    @NotBlank
    @Schema(description = "title of the feedback template", required = true)
    private String title;

    @Nullable
    @Schema(description = "description of the feedback template")
    private String description;

    @NotBlank
    @Schema(description = "UUID of person who created the feedback template", required = true)
    private UUID creatorId;

    @NotBlank
    @Schema(description = "whether or not the template is allowed to be used for a feedback request", required = true)
    private Boolean active;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public UUID getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(UUID creatorId) {
        this.creatorId = creatorId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
