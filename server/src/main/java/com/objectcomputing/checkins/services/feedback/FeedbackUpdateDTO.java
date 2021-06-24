package com.objectcomputing.checkins.services.feedback;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class FeedbackUpdateDTO {

    @NotNull
    @Schema(description = "id of the entry the feedback is associated with", required = true)
    private UUID id;

    @NotBlank
    @Schema(description = "the updated content of the feedback", required = true)
    private String content;

    @NotNull
    @Schema(description = "whether the feedback is public or private", required = true)
    private Boolean confidential;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getConfidential() {
        return confidential;
    }

    public void setConfidential(Boolean confidential) {
        this.confidential = confidential;
    }

}
