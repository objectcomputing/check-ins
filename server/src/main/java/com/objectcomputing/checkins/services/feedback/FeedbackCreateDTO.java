package com.objectcomputing.checkins.services.feedback;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;
@Introspected
public class FeedbackCreateDTO {

    @NotBlank
    @Schema(description = "content of the feedback", required = true)
    private String content;

    @NotNull
    @Schema(description = "id of member profile to whom the feedback was sent", required = true)
    private UUID sentTo;

    @NotNull
    @Schema(description = "whether the feedback is public or private", required = true)
    private Boolean confidential;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UUID getSentTo() {
        return sentTo;
    }

    public void setSentTo(UUID sentTo) {
        this.sentTo = sentTo;
    }

    public Boolean getConfidential() {
        return confidential;
    }

    public void setConfidential(Boolean confidential) {
        this.confidential = confidential;
    }

}
