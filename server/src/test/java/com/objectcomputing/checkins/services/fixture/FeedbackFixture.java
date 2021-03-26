package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.feedback.Feedback;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

import java.time.LocalDateTime;

public interface FeedbackFixture extends RepositoryFixture {
    default Feedback createFeedback(String content, MemberProfile to, MemberProfile from, boolean confidential) {
        return getFeedbackRepository().save(new Feedback(content, to.getId(), from.getId(),
                confidential, LocalDateTime.now(), null));
    }
}
