package com.objectcomputing.checkins.services.feedback.suggestions;

import com.sun.istack.Nullable;

import java.util.Objects;
import java.util.UUID;
import javax.validation.constraints.NotNull;

public class FeedbackSuggestionDTO {

    @Nullable
    private String reason;
    @Nullable
    private UUID id;

    public FeedbackSuggestionDTO() {
        this.reason = null;
        this.id = null;
    }

    public FeedbackSuggestionDTO(String reason, UUID profileId) {
        this.reason = reason;
        this.id = profileId;
    }

    public void setProfileId(UUID profileId) {
        this.id = profileId;
    }

    public String getReason() {
        return reason;
    }

    public UUID getProfileId() {
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
