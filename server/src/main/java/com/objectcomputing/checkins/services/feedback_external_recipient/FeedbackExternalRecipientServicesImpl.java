package com.objectcomputing.checkins.services.feedback_external_recipient;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;

import java.util.*;

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
    public List<FeedbackExternalRecipient> findAll() {
        return feedbackExternalRecipientRepository.findAll();
    }

    /*
    @Override
    public List<FeedbackExternalRecipient> findByValues(@Nullable String email, @Nullable String firstName, @Nullable String lastName, @Nullable String companyName) {
        List<FeedbackExternalRecipient> feedbackExternalRecipients = new ArrayList<>(feedbackExternalRecipientRepository.search());

        return feedbackExternalRecipients;
    }
    */

}
