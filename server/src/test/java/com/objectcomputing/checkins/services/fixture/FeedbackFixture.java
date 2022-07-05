package com.objectcomputing.checkins.services.fixture;
import java.util.UUID;
import com.objectcomputing.checkins.services.feedback.suggestions.FeedbackSuggestionDTO;

public interface FeedbackFixture extends RepositoryFixture {

    default FeedbackSuggestionDTO createFeedbackSuggestion(String reason, UUID profileId) {
        return new FeedbackSuggestionDTO(reason, profileId);

    }
}
