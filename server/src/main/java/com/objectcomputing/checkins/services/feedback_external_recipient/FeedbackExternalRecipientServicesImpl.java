package com.objectcomputing.checkins.services.feedback_external_recipient;

import jakarta.inject.Singleton;

import java.util.List;
import java.util.UUID;

@Singleton
public class FeedbackExternalRecipientServicesImpl implements FeedbackExternalRecipientServices {

    private final FeedbackExternalRecipientRepository feedbackExternalRecipientRepository;

    public FeedbackExternalRecipientServicesImpl(FeedbackExternalRecipientRepository feedbackExternalRecipientRepository) {
        this.feedbackExternalRecipientRepository = feedbackExternalRecipientRepository;
    }

    @Override
    public FeedbackExternalRecipient save(FeedbackExternalRecipient feedbackExternalRecipient) {
        return feedbackExternalRecipientRepository.save(feedbackExternalRecipient);
    }

    @Override
    public FeedbackExternalRecipient update(FeedbackExternalRecipientUpdateDTO feedbackExternalRecipientUpdateDTO) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public FeedbackExternalRecipient getById(UUID id) {
        return null;
    }

    @Override
    public List<FeedbackExternalRecipient> findByValues(String email, String firstName, String lastName, String companyName) {
        return List.of();
    }
}
