package com.objectcomputing.checkins.services.feedback.suggestions;

import java.util.Objects;
import java.util.UUID;
import javax.validation.constraints.NotNull;

public class FeedbackSuggestionDTO {

    @NotNull
    private String reason;
    @NotNull
    private UUID profileId;

    public FeedbackSuggestionDTO() { }

    public FeedbackSuggestionDTO(String reason, UUID profileId) {
        this.reason = reason;
        this.profileId = profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public String getReason() {
        return reason;
    }

    public UUID getProfileId() {
        return profileId;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedbackSuggestionDTO that = (FeedbackSuggestionDTO) o;
        return Objects.equals(reason, that.reason) && Objects.equals(profileId, that.profileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reason, profileId);
    }
}
