package com.objectcomputing.checkins.services.feedback_question;

import java.util.UUID;

public interface FeedbackQuestionServices {

    FeedbackQuestion save(FeedbackQuestion feedbackQuestion);

    FeedbackQuestion getById(UUID id);

}
