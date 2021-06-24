package com.objectcomputing.checkins.services.feedback;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Introspected
public class FeedbackResponseDTO {

    @NotNull
    @Schema(description = "id of the entry the feedback is associated with", required = true)
    private UUID id;

    @NotBlank
    @Schema(description = "the content of the feedback", required = true)
    private String content;

    @NotNull
    @Schema(description = "id of member profile to whom the feedback was sent", required = true)
    private UUID sentTo;

    @NotNull
    @Schema(description = "id of member profile who created the feedback", required = true)
    private UUID sentBy;

    @NotNull
    @Schema(description = "whether the feedback is public or private", required = true)
    private Boolean confidential;

    @NotNull
    @Schema(description = "date when the feedback was created", required = true)
    private LocalDateTime createdOn;

    @Nullable
    @Schema(description = "date of the latest update to the feedback", required = true)
    private LocalDateTime updatedOn;

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

    public UUID getSentTo() {
        return sentTo;
    }

    public void setSentTo(UUID sentTo) {
        this.sentTo = sentTo;
    }

    public UUID getSentBy() {
        return sentBy;
    }

    public void setSentBy(UUID sentBy) {
        this.sentBy = sentBy;
    }

    public Boolean getConfidential() {
        return confidential;
    }

    public void setConfidential(Boolean confidential) {
        this.confidential = confidential;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    @Nullable
    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(@Nullable LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }
}
