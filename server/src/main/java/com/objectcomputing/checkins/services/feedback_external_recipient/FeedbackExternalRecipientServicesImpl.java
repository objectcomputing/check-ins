package com.objectcomputing.checkins.services.feedback_external_recipient;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;
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
        Optional<FeedbackExternalRecipient> optional = feedbackExternalRecipientRepository.findById(id);
        if (optional.isEmpty()) {
            throw new NotFoundException("No external recipient found with id: " + id);
        }
        FeedbackExternalRecipient feedbackExternalRecipient = optional.get();
        return feedbackExternalRecipient;
    }

    @Override
    public List<FeedbackExternalRecipient> findByValues(String email, String firstName, String lastName, String companyName) {
        return List.of();
    }
}
