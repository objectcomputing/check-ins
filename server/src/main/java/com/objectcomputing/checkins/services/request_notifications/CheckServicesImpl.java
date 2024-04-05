package com.objectcomputing.checkins.services.request_notifications;

import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestRepository;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServicesImpl;
import jakarta.inject.Singleton;

import java.time.LocalDate;
import java.util.List;

@Singleton
public class CheckServicesImpl implements CheckServices {
    private final FeedbackRequestServicesImpl feedbackRequestServices;
    private final FeedbackRequestRepository feedbackRequestRepository;

    public CheckServicesImpl(FeedbackRequestServicesImpl feedbackRequestServices,
                             FeedbackRequestRepository feedbackRequestRepository) {
        this.feedbackRequestServices = feedbackRequestServices;
        this.feedbackRequestRepository = feedbackRequestRepository;
    }

    @Override
    public boolean sendScheduledEmails() {
        LocalDate today = LocalDate.now();
        List<FeedbackRequest> feedbackRequests = feedbackRequestRepository.findBySendDateBeforeAndStatusEqual(today, "pending");
        for (FeedbackRequest req: feedbackRequests) {
            feedbackRequestServices.sendNewRequestEmail(req);
            req.setStatus("sent");
            feedbackRequestRepository.update(req);
        }
        return true;
    }

}
