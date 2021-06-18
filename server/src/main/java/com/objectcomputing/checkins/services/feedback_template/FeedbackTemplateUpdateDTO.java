package com.objectcomputing.checkins.services.feedback_template;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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


}
