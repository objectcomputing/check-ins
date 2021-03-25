package com.objectcomputing.checkins.services.feedback;

import java.util.UUID;

public interface FeedbackServices {
    Feedback save(Feedback feedback);

    Boolean delete(UUID id);
}
