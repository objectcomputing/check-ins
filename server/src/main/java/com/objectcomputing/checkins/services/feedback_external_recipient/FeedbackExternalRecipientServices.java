package com.objectcomputing.checkins.services.feedback_external_recipient;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FeedbackExternalRecipientServices {
    FeedbackExternalRecipient save(FeedbackExternalRecipient feedbackExternalRecipient);

    FeedbackExternalRecipient update(FeedbackExternalRecipientUpdateDTO feedbackExternalRecipientUpdateDTO);

    void delete(UUID id);

    FeedbackExternalRecipient getById(UUID id);

    List<FeedbackExternalRecipient> findByValues(String email, String firstName, String lastName, String companyName);

}
