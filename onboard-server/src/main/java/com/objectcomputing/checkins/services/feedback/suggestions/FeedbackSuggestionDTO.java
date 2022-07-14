package com.objectcomputing.checkins.services.feedback.suggestions;

import io.micronaut.core.annotation.Nullable;

import java.util.Objects;
import java.util.UUID;

public class FeedbackSuggestionDTO {

    @Nullable
    private String reason;
    @Nullable
    private UUID id;

    public FeedbackSuggestionDTO() {
        this.reason = null;
        this.id = null;
    }

    public FeedbackSuggestionDTO(String reason, UUID id) {
        this.reason = reason;
        this.id = id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public UUID getId() {
        return id;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedbackSuggestionDTO that = (FeedbackSuggestionDTO) o;
        return Objects.equals(reason, that.reason) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reason, id);
    }
}
