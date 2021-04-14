package com.objectcomputing.checkins.services.feedback;

import java.util.List;
import java.util.UUID;

public interface FeedbackServices {
    Feedback save(Feedback feedback);

    Boolean delete(UUID id);

    Feedback getById(UUID id);

    List<Feedback> getByValues(UUID sentBy, UUID sentTo, Boolean confidential);
}
