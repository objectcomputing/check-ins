package com.objectcomputing.checkins.services.feedback_question;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import javax.inject.Singleton;
import java.util.UUID;

@Singleton
public class FeedbackQuestionServicesImpl implements FeedbackQuestionServices {

    private final FeedbackQuestionRepository feedbackQuestionRepository;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;

    public FeedbackQuestionServicesImpl(FeedbackQuestionRepository feedbackQuestionRepository,
                                        CurrentUserServices currentUserServices,
                                        MemberProfileServices memberProfileServices) {
        this.feedbackQuestionRepository = feedbackQuestionRepository;
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
    }

    @Override
    public FeedbackQuestion save(FeedbackQuestion feedbackQuestion) {
        return null;
    }

    @Override
    public FeedbackQuestion getById(UUID id) {
        return null;
    }

    public boolean createIsPermitted(UUID templateId) {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return currentUserId != null;
    }

    public boolean getIsPermitted() {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return currentUserId != null;
    }
}
