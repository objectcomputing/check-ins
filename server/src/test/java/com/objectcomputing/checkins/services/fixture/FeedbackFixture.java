package com.objectcomputing.checkins.services.fixture;
import java.util.UUID;
import com.objectcomputing.checkins.services.feedback.Feedback;
import com.objectcomputing.checkins.services.feedback.suggestions.FeedbackSuggestionDTO;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

public interface FeedbackFixture extends RepositoryFixture {
    default Feedback createFeedback(String content, MemberProfile to, MemberProfile from, boolean confidential) {
        return getFeedbackRepository().save(new Feedback(content, to.getId(), from.getId(),
                confidential));
    }

    default FeedbackSuggestionDTO createFeedbackSuggestion(String reason, UUID profileId) {
        return new FeedbackSuggestionDTO(reason, profileId);

    }
}
